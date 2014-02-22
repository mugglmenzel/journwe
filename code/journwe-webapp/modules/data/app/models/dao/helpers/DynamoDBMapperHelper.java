package models.dao.helpers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.BucketNameUtils;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.dao.common.PersistenceHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: markus
 * Date: 19/12/13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class DynamoDBMapperHelper extends DynamoDBMapper {

    private static AmazonDynamoDB dynamoDB = PersistenceHelper.getDynamoDBClient();
    private final DynamoDBReflectorHelper reflector = new DynamoDBReflectorHelper();

    public DynamoDBMapperHelper(AmazonDynamoDB dynamoDB) {
        super(dynamoDB);
    }

    public static AmazonDynamoDB getDynamoDB() {
        return dynamoDB;
    }

    /**
     * Use the default dynamoDB.
     */
    public DynamoDBMapperHelper() {
        super(dynamoDB);
    }

    /**
     * Returns the name of the primary hash key.
     */
    public String getPrimaryHashKeyName(Class<?> clazz) {
        return reflector.getPrimaryHashKeyName(clazz);
    }

    /**
     * Returns the name of the primary range key.
     */
    public String getPrimaryRangeKeyName(Class<?> clazz) {
        return reflector.getPrimaryRangeKeyName(clazz);
    }

    public String getTableName(Class<?> clazz) {
        DynamoDBTable table = reflector.getTable(clazz);
        return table.tableName();
    }

    /**
     * Returns a key map for the key object given.
     *
     * @param keyObject
     * The key object, corresponding to an item in a dynamo table.
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, AttributeValue> getKey(T keyObject) {
        return getKey(keyObject, (Class<T>)keyObject.getClass());
    }

    /**
     * Returns a key map for the key object given.
     *
     * @param keyObject
     * The key object, corresponding to an item in a dynamo table.
     */
    public <T> Map<String, AttributeValue> getKey(T keyObject, Class<T> clazz) {
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        for (Method keyGetter : reflector.getPrimaryKeyGetters(clazz)) {
            Object getterResult = safeInvoke(keyGetter, keyObject);
            AttributeValue keyAttributeValue = getSimpleAttributeValue(keyGetter, getterResult);
            if (keyAttributeValue == null) {
                throw new DynamoDBMappingException("Null key found for " + keyGetter);
            }
            key.put(reflector.getAttributeName(keyGetter), keyAttributeValue);
        }

        if ( key.isEmpty() ) {
            throw new DynamoDBMappingException("Class must be annotated with " + DynamoDBHashKey.class + " and "
                    + DynamoDBRangeKey.class);
        }
        return key;
    }

    /**
     * Swallows the checked exceptions around Method.invoke and repackages them
     * as {@link DynamoDBMappingException}
     */
    private Object safeInvoke(Method method, Object object, Object... arguments) {
        try {
            return method.invoke(object, arguments);
        } catch ( IllegalAccessException e ) {
            throw new DynamoDBMappingException("Couldn't invoke " + method, e);
        } catch ( IllegalArgumentException e ) {
            throw new DynamoDBMappingException("Couldn't invoke " + method, e);
        } catch ( InvocationTargetException e ) {
            throw new DynamoDBMappingException("Couldn't invoke " + method, e);
        }
    }

    /**
     * Returns an {@link AttributeValue} corresponding to the getter and return
     * result given, treating it as a non-versioned attribute.
     */
    private AttributeValue getSimpleAttributeValue(final Method getter, final Object getterReturnResult) {
        if ( getterReturnResult == null )
            return null;

        ArgumentMarshaller marshaller = reflector.getArgumentMarshaller(getter);
        return marshaller.marshall(getterReturnResult);
    }


    /**
     * Helper classes copied from AWS Library
     *
     */

    /**
     * Reflection assistant for {@link DynamoDBMapper}
     */
    public class DynamoDBReflectorHelper {

        /*
         * Several caches for performance. Collectively, they can make this class
         * over twice as fast.
         */
        private final Map<Class<?>, Collection<Method>> getterCache = new HashMap<Class<?>, Collection<Method>>();
        private final Map<Class<?>, Method> primaryHashKeyGetterCache = new HashMap<Class<?>, Method>();
        private final Map<Class<?>, Method> primaryRangeKeyGetterCache = new HashMap<Class<?>, Method>();

        /*
    * All caches keyed by a Method use the getter for a particular mapped
    * property
    */
        private final Map<Method, Method> setterCache = new HashMap<Method, Method>();
        private final Map<Method, String> attributeNameCache = new HashMap<Method, String>();
        private final Map<Method, ArgumentUnmarshaller> argumentUnmarshallerCache = new HashMap<Method, ArgumentUnmarshaller>();
        private final Map<Method, ArgumentMarshaller> argumentMarshallerCache = new HashMap<Method, ArgumentMarshaller>();
        private final Map<Method, ArgumentMarshaller> versionArgumentMarshallerCache = new HashMap<Method, ArgumentMarshaller>();
        private final Map<Method, ArgumentMarshaller> keyArgumentMarshallerCache = new HashMap<Method, ArgumentMarshaller>();
        private final Map<Method, Boolean> versionAttributeGetterCache = new HashMap<Method, Boolean>();
        private final Map<Method, Boolean> autoGeneratedKeyGetterCache = new HashMap<Method, Boolean>();

        /**
         * Returns the set of getter methods which are relevant when marshalling or
         * unmarshalling an object.
         */
        Collection<Method> getRelevantGetters(Class<?> clazz) {
            synchronized (getterCache) {
                if ( !getterCache.containsKey(clazz) ) {
                    List<Method> relevantGetters = new LinkedList<Method>();
                    for ( Method m : clazz.getMethods() ) {
                        if ( isRelevantGetter(m) ) {
                            relevantGetters.add(m);
                        }
                    }
                    getterCache.put(clazz, relevantGetters);
                }
                return getterCache.get(clazz);
            }
        }

        /**
         * Returns whether the method given is a getter method we should serialize /
         * deserialize to the service. The method must begin with "get" or "is",
         * have no arguments, belong to a class that declares its table, and not be
         * marked ignored.
         */
        private boolean isRelevantGetter(Method m) {
            return (m.getName().startsWith("get") || m.getName().startsWith("is"))
                    && m.getParameterTypes().length == 0
                    && m.getDeclaringClass().getAnnotation(DynamoDBTable.class) != null
                    && !m.isAnnotationPresent(DynamoDBIgnore.class);
        }

        /**
         * Returns the annotated {@link DynamoDBRangeKey} getter for the class
         * given, or null if the class doesn't have one.
         */
        <T> Method getPrimaryRangeKeyGetter(Class<T> clazz) {
            synchronized (primaryRangeKeyGetterCache) {
                if ( !primaryRangeKeyGetterCache.containsKey(clazz) ) {
                    Method rangeKeyMethod = null;
                    for ( Method method : getRelevantGetters(clazz) ) {
                        if ( method.getParameterTypes().length == 0 && method.isAnnotationPresent(DynamoDBRangeKey.class)) {
                            rangeKeyMethod = method;
                            break;
                        }
                    }
                    primaryRangeKeyGetterCache.put(clazz, rangeKeyMethod);
                }
                return primaryRangeKeyGetterCache.get(clazz);
            }
        }

        /**
         * Returns all annotated {@link DynamoDBHashKey} and
         * {@link DynamoDBRangeKey} getters for the class given, throwing an
         * exception if there isn't one.
         *
         * TODO: caching
         */
        <T> Collection<Method> getPrimaryKeyGetters(Class<T> clazz) {
            List<Method> keyGetters = new LinkedList<Method>();
            for (Method getter : getRelevantGetters(clazz)) {
                if (getter.isAnnotationPresent(DynamoDBHashKey.class)
                        || getter.isAnnotationPresent(DynamoDBRangeKey.class)) {
                    keyGetters.add(getter);
                }
            }

            return keyGetters;
        }


        /**
         * Returns the annotated {@link DynamoDBHashKey} getter for the class given,
         * throwing an exception if there isn't one.
         */
        <T> Method getPrimaryHashKeyGetter(Class<T> clazz) {
            Method hashKeyMethod;
            synchronized (primaryHashKeyGetterCache) {
                if ( !primaryHashKeyGetterCache.containsKey(clazz) ) {
                    for ( Method method : getRelevantGetters(clazz) ) {
                        if ( method.getParameterTypes().length == 0 && method.isAnnotationPresent(DynamoDBHashKey.class)) {
                            primaryHashKeyGetterCache.put(clazz, method);
                            break;
                        }
                    }
                }
                hashKeyMethod = primaryHashKeyGetterCache.get(clazz);
            }

            if ( hashKeyMethod == null ) {
                throw new DynamoDBMappingException("Public, zero-parameter hash key property must be annotated with "
                        + DynamoDBHashKey.class);
            }
            return hashKeyMethod;
        }

        /**
         * Returns the {@link DynamoDBTable} annotation of the class given, throwing
         * a runtime exception if it isn't annotated.
         */
        <T> DynamoDBTable getTable(Class<T> clazz) {
            DynamoDBTable table = clazz.getAnnotation(DynamoDBTable.class);
            if ( table == null )
                throw new DynamoDBMappingException("Class " + clazz + " must be annotated with " + DynamoDBTable.class);
            return table;
        }

        /**
         * Returns a marshaller that knows how to provide an AttributeValue for the
         * result of the getter given.
         */
        ArgumentMarshaller getArgumentMarshaller(final Method getter) {
            synchronized (argumentMarshallerCache) {
                ArgumentMarshaller marshaller = argumentMarshallerCache.get(getter);
                if ( marshaller != null ) {
                    return marshaller;
                }
                if ( isCustomMarshaller(getter) ) {
                    marshaller = new ArgumentMarshaller() {
                        @Override public AttributeValue marshall(Object obj) {
                            return getCustomerMarshallerAttributeValue(getter, obj);
                        }
                    };
                } else {
                    marshaller = computeArgumentMarshaller(getter);
                }
                argumentMarshallerCache.put(getter, marshaller);
                return marshaller;
            }
        }

        /**
         * Returns whether or not this getter has a custom marshaller
         */
        private boolean isCustomMarshaller(Method getter) {
            return getter.isAnnotationPresent(DynamoDBMarshalling.class);
        }

        /**
         * Returns an attribute value for the getter method with a custom marshaller.
         * Directly returns null when the custom marshaller returns a null String.
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        private AttributeValue getCustomerMarshallerAttributeValue(Method getter, Object getterReturnResult) {
            DynamoDBMarshalling annotation = getter.getAnnotation(DynamoDBMarshalling.class);
            Class<? extends DynamoDBMarshaller<? extends Object>> marshallerClass = annotation.marshallerClass();

            DynamoDBMarshaller marshaller;
            try {
                marshaller = marshallerClass.newInstance();
            } catch ( InstantiationException e ) {
                throw new DynamoDBMappingException("Failed to instantiate custom marshaller for class " + marshallerClass,
                        e);
            } catch ( IllegalAccessException e ) {
                throw new DynamoDBMappingException("Failed to instantiate custom marshaller for class " + marshallerClass,
                        e);
            }
            String stringValue = marshaller.marshall(getterReturnResult);

            if(stringValue == null) {
                return null;
            } else {
                return new AttributeValue().withS(stringValue);
            }
        }

        /**
         * Note this method is synchronized on {@link #argumentMarshallerCache} while being executed.
         */
        private ArgumentMarshaller computeArgumentMarshaller(final Method getter) {
            ArgumentMarshaller marshaller;
            Class<?> returnType = getter.getReturnType();
            if ( Set.class.isAssignableFrom(returnType) ) {
                Type genericType = getter.getGenericReturnType();
                if ( genericType instanceof ParameterizedType) {
                    if ( ((ParameterizedType) genericType).getActualTypeArguments()[0].toString().equals("byte[]") ) {
                        returnType = byte[].class;
                    } else {
                        returnType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                    }
                }

                if ( Date.class.isAssignableFrom(returnType) ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            List<String> timestamps = new LinkedList<String>();
                            for ( Object o : (Set<?>) obj ) {
                                timestamps.add(new DateUtils().formatIso8601Date((Date) o));
                            }
                            return new AttributeValue().withSS(timestamps);
                        }
                    };
                } else if ( Calendar.class.isAssignableFrom(returnType) ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            List<String> timestamps = new LinkedList<String>();
                            for ( Object o : (Set<?>) obj ) {
                                timestamps.add(new DateUtils().formatIso8601Date(((Calendar) o).getTime()));
                            }
                            return new AttributeValue().withSS(timestamps);
                        }
                    };
                } else if ( boolean.class.isAssignableFrom(returnType)
                        || Boolean.class.isAssignableFrom(returnType) ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            List<String> booleanAttributes = new ArrayList<String>();
                            for ( Object b : (Set<?>) obj ) {
                                if ( b == null || !(Boolean) b ) {
                                    booleanAttributes.add("0");
                                } else {
                                    booleanAttributes.add("1");
                                }
                            }
                            return new AttributeValue().withNS(booleanAttributes);
                        }
                    };
                } else if ( returnType.isPrimitive() || Number.class.isAssignableFrom(returnType) ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            List<String> attributes = new ArrayList<String>();
                            for ( Object o : (Set<?>) obj ) {
                                attributes.add(String.valueOf(o));
                            }
                            return new AttributeValue().withNS(attributes);
                        }
                    };
                } else if (ByteBuffer.class.isAssignableFrom(returnType)) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            List<ByteBuffer> attributes = new ArrayList<ByteBuffer>();
                            for ( Object o : (Set<?>) obj ) {
                                attributes.add((ByteBuffer) o);
                            }
                            return new AttributeValue().withBS(attributes);
                        }
                    };
                } else if (byte[].class.isAssignableFrom(returnType)) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            List<ByteBuffer> attributes = new ArrayList<ByteBuffer>();
                            for ( Object o : (Set<?>) obj ) {
                                attributes.add(ByteBuffer.wrap((byte[])o));
                            }
                            return new AttributeValue().withBS(attributes);
                        }
                    };
                } else {
                    // subclass may extend the behavior by overriding the
                    // defaultCollectionArgumentMarshaller method
                    marshaller = defaultCollectionArgumentMarshaller(returnType);
                }
            } else if ( Collection.class.isAssignableFrom(returnType) ) {
                throw new DynamoDBMappingException("Non-set collections aren't supported: "
                        + (getter.getDeclaringClass() + "." + getter.getName()));
            } else { // Non-set return type
                if ( Date.class.isAssignableFrom(returnType) ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            return new AttributeValue().withS(new DateUtils().formatIso8601Date((Date) obj));
                        }
                    };
                } else if ( Calendar.class.isAssignableFrom(returnType) ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            return new AttributeValue().withS(new DateUtils()
                                    .formatIso8601Date(((Calendar) obj).getTime()));
                        }
                    };
                } else if ( boolean.class.isAssignableFrom(returnType)
                        || Boolean.class.isAssignableFrom(returnType) ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            if ( obj == null || !(Boolean) obj ) {
                                return new AttributeValue().withN("0");
                            } else {
                                return new AttributeValue().withN("1");
                            }
                        }
                    };
                } else if ( returnType.isPrimitive() || Number.class.isAssignableFrom(returnType) ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            return new AttributeValue().withN(String.valueOf(obj));
                        }
                    };
                } else if ( returnType == String.class ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            if ( ((String) obj).length() == 0 )
                                return null;
                            return new AttributeValue().withS(String.valueOf(obj));
                        }
                    };
                } else if ( returnType == ByteBuffer.class ) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            return new AttributeValue().withB((ByteBuffer)obj);
                        }
                    };
                } else if ( returnType == byte[].class) {
                    marshaller = new ArgumentMarshaller() {

                        @Override
                        public AttributeValue marshall(Object obj) {
                            return new AttributeValue().withB(ByteBuffer.wrap((byte[])obj));
                        }
                    };
                } else {
                    marshaller = defaultArgumentMarshaller(returnType, getter);
                }
            }
            return marshaller;
        }

        /**
         * Note this method is synchronized on {@link #argumentMarshallerCache} while being executed.
         * @param returnElementType the element of the return type which is known to be a collection
         * @return the default argument marshaller for a collection
         */
        private ArgumentMarshaller defaultCollectionArgumentMarshaller(final Class<?> returnElementType) {
            if ( S3Link.class.isAssignableFrom(returnElementType) ) {
                throw new DynamoDBMappingException("Collection types not permitted for " + S3Link.class);
            } else {
                return new ArgumentMarshaller() {
                    @Override
                    public AttributeValue marshall(Object obj) {
                        List<String> attributes = new ArrayList<String>();
                        for ( Object o : (Set<?>) obj ) {
                            attributes.add(String.valueOf(o));
                        }
                        return new AttributeValue().withSS(attributes);
                    }
                };
            }
        }

        /**
         * Note this method is synchronized on {@link #argumentMarshallerCache} while being executed.
         * @param returnType the return type
         * @return the default argument marshaller
         */
        private ArgumentMarshaller defaultArgumentMarshaller(final Class<?> returnType, final Method getter) {
            if ( returnType == S3Link.class ) {
                return new ArgumentMarshaller() {
                    @Override
                    public AttributeValue marshall(Object obj) {
                        S3Link s3link = (S3Link) obj;
                        if ( s3link.getBucketName() == null || s3link.getKey() == null ) {
                            // insufficient S3 resource specification
                            return null;
                        }
                        String json = s3link.toJson();
                        return new AttributeValue().withS(json);
                    }
                };
            } else {
                throw new DynamoDBMappingException("Unsupported type: " + returnType + " for " + getter);
            }
        }

        /**
         * Returns the attribute name corresponding to the given getter method.
         */
        String getAttributeName(Method getter) {
            synchronized (attributeNameCache) {
                if ( !attributeNameCache.containsKey(getter) ) {

                    // First check for a hash key annotation
                    DynamoDBHashKey hashKeyAnnotation = getter.getAnnotation(DynamoDBHashKey.class);
                    if ( hashKeyAnnotation != null && hashKeyAnnotation.attributeName() != null
                            && hashKeyAnnotation.attributeName().length() > 0 )
                        return hashKeyAnnotation.attributeName();

                    // Then an index hash key
                    DynamoDBIndexHashKey indexHashKey = getter.getAnnotation(DynamoDBIndexHashKey.class);
                    if ( indexHashKey != null && indexHashKey.attributeName() != null && indexHashKey.attributeName().length() > 0 )
                        return indexHashKey.attributeName();

                    // Then a primary range key
                    DynamoDBRangeKey rangeKey = getter.getAnnotation(DynamoDBRangeKey.class);
                    if ( rangeKey != null && rangeKey.attributeName() != null && rangeKey.attributeName().length() > 0 )
                        return rangeKey.attributeName();

                    // Then an index range key
                    DynamoDBIndexRangeKey indexRangeKey = getter.getAnnotation(DynamoDBIndexRangeKey.class);
                    if ( indexRangeKey != null && indexRangeKey.attributeName() != null && indexRangeKey.attributeName().length() > 0 )
                        return indexRangeKey.attributeName();

                    // Then an attribute
                    DynamoDBAttribute attribute = getter.getAnnotation(DynamoDBAttribute.class);
                    if ( attribute != null && attribute.attributeName() != null && attribute.attributeName().length() > 0 )
                        return attribute.attributeName();

                    // Finally a version attribute
                    DynamoDBVersionAttribute version = getter.getAnnotation(DynamoDBVersionAttribute.class);
                    if ( version != null && version.attributeName() != null && version.attributeName().length() > 0 )
                        return version.attributeName();

                    // Default to method name
                    String attributeName = null;
                    if ( getter.getName().startsWith("get") ) {
                        attributeName = getter.getName().substring("get".length());
                    } else if ( getter.getName().startsWith("is") ) {
                        attributeName = getter.getName().substring("is".length());
                    } else {
                        throw new DynamoDBMappingException("Getter must begin with 'get' or 'is'");
                    }

                    // Lowercase the first letter of the name
                    attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
                    attributeNameCache.put(getter, attributeName);
                }
                return attributeNameCache.get(getter);
            }
        }

        /**
         * Returns the name of the primary hash key.
         */
        String getPrimaryHashKeyName(Class<?> clazz) {
            return getAttributeName(getPrimaryHashKeyGetter(clazz));
        }

        /**
         * Returns the name of the primary range key.
         */
        String getPrimaryRangeKeyName(Class<?> clazz) {
            return getAttributeName(getPrimaryRangeKeyGetter(clazz));
        }

    }

    static class S3Link {
        private final S3ClientCache s3cc;
        private final ID id;

        S3Link(S3ClientCache s3cc, String bucketName, String key) {
            this(s3cc, new ID(bucketName, key));
        }

        S3Link(S3ClientCache s3cc, Region region, String bucketName, String key) {
            this(s3cc, new ID(region, bucketName, key));
        }

        private S3Link(S3ClientCache s3cc, ID id) {
            this.s3cc = s3cc;
            this.id = id;

            if ( s3cc == null ) {
                throw new IllegalArgumentException("S3ClientCache must be configured for use with S3Link");
            }
            if ( id == null || id.getBucket() == null || id.getKey() == null ) {
                throw new IllegalArgumentException("Bucket and key must be specified for S3Link");
            }
        }

        public String getKey() {
            return id.getKey();
        }

        public String getBucketName() {
            return id.getBucket();
        }

        public Region getS3Region() {
            return Region.fromValue(id.getRegionId());
        }

        /**
         * Serializes into a JSON string.
         *
         * @return The string representation of the link to the S3 resource.
         */
        String toJson() {
            return id.toJson();
        }

        /**
         * Deserializes from a JSON string.
         */
        static S3Link fromJson(S3ClientCache s3cc, String json) {
            ID id = Jackson.fromJsonString(json, ID.class);
            return new S3Link(s3cc, id);
        }

        public AmazonS3Client getAmazonS3Client() {
            return s3cc.getClient(getS3Region());
        }

        public TransferManager getTransferManager() {
            return s3cc.getTransferManager(getS3Region());
        }

        /**
         * Convenience method to synchronously upload from the given file to the
         * Amazon S3 object represented by this S3Link.
         *
         * @param source
         *            source file to upload from
         *
         * @return A {@link com.amazonaws.services.s3.model.PutObjectResult} object containing the information
         *         returned by Amazon S3 for the newly created object.
         */
        public PutObjectResult uploadFrom(final File source) {
            return uploadFrom0(source, null);
        }

        /**
         * Same as {@link #uploadFrom(File)} but allows specifying a
         * request metric collector.
         */
        public PutObjectResult uploadFrom(final File source,
                                          RequestMetricCollector requestMetricCollector) {
            return uploadFrom0(source, requestMetricCollector);
        }

        private PutObjectResult uploadFrom0(final File source,
                                            RequestMetricCollector requestMetricCollector) {
            PutObjectRequest req = new PutObjectRequest(getBucketName(), getKey(),
                    source).withRequestMetricCollector(requestMetricCollector);
            return getAmazonS3Client().putObject(req);
        }

        /**
         * Convenience method to synchronously upload from the given buffer to the
         * Amazon S3 object represented by this S3Link.
         *
         * @param buffer
         *            The buffer containing the data to upload.
         *
         * @return A {@link PutObjectResult} object containing the information
         *         returned by Amazon S3 for the newly created object.
         */
        public PutObjectResult uploadFrom(final byte[] buffer) {
            return uploadFrom0(buffer, null);
        }

        /**
         * Same as {@link #uploadFrom(byte[])} but allows specifying a
         * request metric collector.
         */
        public PutObjectResult uploadFrom(final byte[] buffer,
                                          RequestMetricCollector requestMetricCollector) {
            return uploadFrom0(buffer, requestMetricCollector);
        }

        private PutObjectResult uploadFrom0(final byte[] buffer,
                                            RequestMetricCollector requestMetricCollector) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(buffer.length);
            PutObjectRequest req = new PutObjectRequest(getBucketName(), getKey(),
                    new ByteArrayInputStream(buffer), objectMetadata)
                    .withRequestMetricCollector(requestMetricCollector);
            return getAmazonS3Client().putObject(req);
        }

        /**
         * Sets the access control list for the object represented by this S3Link.
         *
         * Note: Executing this method requires that the object already exists in
         * Amazon S3.
         *
         * @param acl
         *            The access control list describing the new permissions for the
         *            object represented by this S3Link.
         */
        public void setAcl(CannedAccessControlList acl) {
            setAcl0(acl, null);
        }

        public void setAcl(CannedAccessControlList acl, RequestMetricCollector col) {
            setAcl0(acl, col);
        }

        private void setAcl0(CannedAccessControlList acl, RequestMetricCollector col) {
            getAmazonS3Client()
                    .setObjectAcl(getBucketName(), getKey(), null, acl, col);
        }

        /**
         * Sets the access control list for the object represented by this S3Link.
         *
         * Note: Executing this method requires that the object already exists in
         * Amazon S3.
         *
         * @param acl
         *            The access control list describing the new permissions for the
         *            object represented by this S3Link.
         */
        public void setAcl(AccessControlList acl) {
            setAcl0(acl, null);
        }

        /**
         * Same as {@link #setAcl(AccessControlList)} but allows specifying a
         * request metric collector.
         */
        public void setAcl(AccessControlList acl,
                           RequestMetricCollector requestMetricCollector) {
            setAcl0(acl, requestMetricCollector);
        }

        private void setAcl0(AccessControlList acl,
                             RequestMetricCollector requestMetricCollector) {
            getAmazonS3Client().setObjectAcl(getBucketName(), getKey(), null, acl,
                    requestMetricCollector);
        }

        /**
         * Returns a URL for the location of the object represented by this S3Link.
         * <p>
         * If the object represented by this S3Link has public read permissions (ex:
         * {@link CannedAccessControlList#PublicRead}), then this URL can be
         * directly accessed to retrieve the object data.
         *
         * @return A URL for the location of the object represented by this S3Link.
         */
        public URL getUrl() {
            return getAmazonS3Client().getUrl(getBucketName(), getKey());
        }

        /**
         * Convenient method to synchronously download to the specified file from
         * the S3 object represented by this S3Link.
         *
         * @param destination destination file to download to
         *
         * @return All S3 object metadata for the specified object.
         * Returns null if constraints were specified but not met.
         */
        public ObjectMetadata downloadTo(final File destination) {
            return downloadTo0(destination, null);
        }

        /**
         * Same as {@link #downloadTo(File)} but allows specifying a
         * request metric collector.
         */
        public ObjectMetadata downloadTo(final File destination,
                                         RequestMetricCollector requestMetricCollector) {
            return downloadTo0(destination, requestMetricCollector);
        }

        private ObjectMetadata downloadTo0(final File destination,
                                           RequestMetricCollector requestMetricCollector) {
            GetObjectRequest req = new GetObjectRequest(getBucketName(), getKey())
                    .withRequestMetricCollector(requestMetricCollector);
            return getAmazonS3Client().getObject(req, destination);
        }

        /**
         * Downloads the data from the object represented by this S3Link to the
         * specified output stream.
         *
         * @param output
         *            The output stream to write the object's data to.
         *
         * @return The object's metadata.
         */
        public ObjectMetadata downloadTo(final OutputStream output) {
            return downloadTo0(output, null);
        }

        /**
         * Same as {@link #downloadTo(OutputStream)} but allows specifying a
         * request metric collector.
         */
        public ObjectMetadata downloadTo(final OutputStream output,
                                         RequestMetricCollector requestMetricCollector) {
            return downloadTo0(output, requestMetricCollector);
        }

        private ObjectMetadata downloadTo0(final OutputStream output,
                                           RequestMetricCollector requestMetricCollector) {
            GetObjectRequest req = new GetObjectRequest(getBucketName(), getKey())
                    .withRequestMetricCollector(requestMetricCollector);
            S3Object s3Object = getAmazonS3Client().getObject(req);
            S3ObjectInputStream objectContent = s3Object.getObjectContent();

            try {
                byte[] buffer = new byte[1024 * 10];
                int bytesRead = -1;
                while ((bytesRead = objectContent.read(buffer)) > -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException ioe) {
                try {
                    objectContent.abort();
                } catch (IOException e) {}
                throw new AmazonClientException("Unable to transfer content from Amazon S3 to the output stream", ioe);
            } finally {
                try { objectContent.close(); } catch (IOException ioe) {}
            }

            return s3Object.getObjectMetadata();
        }

        /**
         * JSON wrapper of an {@link S3Link} identifier,
         * which consists of the S3 region id, bucket name and key.
         * Sample JSON serialized form:
         * <pre>
         * {"s3":{"bucket":"mybucket","key":"mykey","region":"us-west-2"}}
         * {"s3":{"bucket":"mybucket","key":"mykey","region":null}}
         * </pre>
         * Note for S3 a null region means US standard.
         * <p>
         *  @see Region#US_Standard
         */
        static class ID {
            @JsonProperty("s3")
            private S3 s3;

            ID() {} // used by Jackson to unmarshall
            ID(String bucketName, String key) {
                this.s3 = new S3(bucketName, key);
            }
            ID(Region region, String bucketName, String key) {
                this.s3 = new S3(region, bucketName, key);
            }
            ID(S3 s3) {
                this.s3 = s3;
            }
            @JsonProperty("s3")
            public S3 getS3() {
                return s3;
            }
            @JsonIgnore
            public String getRegionId() {
                return s3.getRegionId();
            }
            @JsonIgnore
            public String getBucket() {
                return s3.getBucket();
            }
            @JsonIgnore
            public String getKey() {
                return s3.getKey();
            }
            String toJson() {
                return Jackson.toJsonString(this);
            }
        }

        /**
         * Internal class for JSON serialization purposes.
         * <p>
         * @see ID
         */
        private static class S3 {
            /**
             * The region id of {@link Region} where the S3 object is stored.
             */
            @JsonProperty("region")
            private String regionId;
            /**
             * The name of the S3 bucket containing the object to retrieve.
             */
            @JsonProperty("bucket")
            private String bucket;

            /**
             * The key under which the desired S3 object is stored.
             */
            @JsonProperty("key")
            private String key;

            S3() {}  // used by Jackson to unmarshall
            /**
             * Constructs a new {@link S3} with all the required parameters.
             *
             * @param bucket
             *            The name of the bucket containing the desired object.
             * @param key
             *            The key in the specified bucket under which the object is
             *            stored.
             */
            S3(String bucket, String key) {
                this(null, bucket, key);
            }

            /**
             * Constructs a new {@link S3} with all the required parameters.
             *
             * @param bucket
             *            The name of the bucket containing the desired object.
             * @param key
             *            The key in the specified bucket under which the object is
             *            stored.
             */
            S3(Region region, String bucket, String key) {
                if ( region == null ) {
                    if ( BucketNameUtils.isDNSBucketName(bucket) ) {
                        this.regionId = Region.US_Standard.getFirstRegionId();
                    } else {
                        throw new IllegalArgumentException("Region must be specified for bucket that cannot be addressed using virtual host style");
                    }
                } else {
                    this.regionId = region.getFirstRegionId();
                }
                this.bucket = bucket;
                this.key = key;
            }

            /**
             * Gets the name of the bucket containing the object to be downloaded.
             *
             * @return The name of the bucket containing the object to be downloaded.
             */
            @JsonProperty("bucket")
            public String getBucket() {
                return bucket;
            }

            /**
             * Gets the key under which the object to be downloaded is stored.
             *
             * @return The key under which the object to be downloaded is stored.
             */
            @JsonProperty("key")
            public String getKey() {
                return key;
            }

            @JsonProperty("region")
            public String getRegionId() {
                return regionId;
            }
        }
    }

    interface ArgumentMarshaller {

        /**
         * Marhsalls the object given into an AttributeValue.
         */
        public AttributeValue marshall(Object obj);
    }
}
