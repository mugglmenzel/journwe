package models.dao.manytomany;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import models.dao.common.PersistenceHelper;
import models.dao.helpers.DynamoDBMapperHelper;
import play.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * M-to-N Many-to-Many Relationship Mapping.
 */
public class ManyToManyDAO<M, N> {

    private static DynamoDBMapperHelper dynamoDbMapperHelper = new DynamoDBMapperHelper();
    private static AmazonDynamoDB dynamoDB = PersistenceHelper.getDynamoDBClient();

    private Class<M> clazzM;
    private Class<N> clazzN;

    private String mHashIdName;
    private String nHashIdName;
    private String mTableName;
    private String nTableName;
    private String mToNTableName;
    private String nToMTableName;

    public ManyToManyDAO(Class<M> clazzM, Class<N> clazzN) {
        this.clazzM = clazzM;
        this.clazzN = clazzN;
        mTableName = dynamoDbMapperHelper.getTableName(clazzM);
        nTableName = dynamoDbMapperHelper.getTableName(clazzN);
        mHashIdName = mTableName + "-" + dynamoDbMapperHelper.getPrimaryHashKeyName(clazzM);
        nHashIdName = nTableName + "-" + dynamoDbMapperHelper.getPrimaryHashKeyName(clazzN);
        mToNTableName = dynamoDbMapperHelper.getTableName(clazzM) + "-to-" + dynamoDbMapperHelper.getTableName(clazzN);
        nToMTableName = dynamoDbMapperHelper.getTableName(clazzN) + "-to-" + dynamoDbMapperHelper.getTableName(clazzM);
    }

    /**
     * Create two relationships: M-to-N and N-to-M. You must save each object, i.e., obj1 and obj2, yourself!
     * This is not done by this method because you might want to save the relationships at a different time then
     * saving the objects.
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public boolean createManyToManyRelationship(M obj1, N obj2) {
        Map<String, AttributeValue> key1 = dynamoDbMapperHelper.getKey(obj1);
        Map<String, AttributeValue> key2 = dynamoDbMapperHelper.getKey(obj2);

        try {
            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put(mHashIdName, key1.get(dynamoDbMapperHelper.getPrimaryHashKeyName(clazzM)));
            item.put(nHashIdName, key2.get(dynamoDbMapperHelper.getPrimaryHashKeyName(clazzN)));

            PutItemRequest putItemRequest1 = new PutItemRequest()
                    .withTableName(mToNTableName)
                    .withItem(item);
            AmazonDynamoDB dynamoBD = DynamoDBMapperHelper.getDynamoDB();
            PutItemResult result1 = dynamoBD.putItem(putItemRequest1);
            Logger.debug("Saved many-to-many-relationship table item in " + mToNTableName + ": " + result1);
            PutItemRequest putItemRequest2 = new PutItemRequest()
                    .withTableName(nToMTableName)
                    .withItem(item);
            PutItemResult result2 = dynamoBD.putItem(putItemRequest2);
            Logger.debug("Saved many-to-many-relationship table item in " + nToMTableName + ": " + result2);
        } catch (AmazonServiceException ase) {
            Logger.error("Saving many-to-many-relationship item failed.");
            return false;
        }

        return true;
    }

    /**
     * List all M objects that belong to primary key hashId after range key lastRangeId.
     */
    public Integer countM(String hashId, String lastRangeId) {
        return count(hashId, lastRangeId, -1, true);
    }

    /**
     * List all M objects that belong to primary key hashId after range key lastRangeId.
     */
    public List<M> listM(String hashId, String lastRangeId, int limit) {
        return (List<M>) list(hashId, lastRangeId, limit, true);
    }

    /**
     * List all N objects that belong to primary key hashId after range key lastRangeId.
     */
    public Integer countN(String hashId, String lastRangeId) {
        return count(hashId, lastRangeId, -1, false);
    }

    /**
     * List all N objects that belong to primary key hashId after range key lastRangeId.
     */
    public List<N> listN(String hashId, String lastRangeId, int limit) {
        return (List<N>) list(hashId, lastRangeId, limit, false);
    }

    /**
     * Delete relations between objects obj1 and obj2. You must delete the objects yourself! Maybe you want to delete
     * them at a different time than deleting the relationship.
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public boolean deleteManyToManyRelationship(M obj1, N obj2) {
        Map<String, AttributeValue> key1 = dynamoDbMapperHelper.getKey(obj1);
        Map<String, AttributeValue> key2 = dynamoDbMapperHelper.getKey(obj2);

        try {
            Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put(mHashIdName, key1.get(dynamoDbMapperHelper.getPrimaryHashKeyName(clazzM)));
            key.put(nHashIdName, key2.get(dynamoDbMapperHelper.getPrimaryHashKeyName(clazzN)));

            DeleteItemRequest deleteItemRequest1 = new DeleteItemRequest()
                    .withTableName(mToNTableName).withKey(key);
            DeleteItemResult result1 = dynamoDB.deleteItem(deleteItemRequest1);

            Logger.debug("Deleted many-to-many-relationship table item in " + mToNTableName + ": " + result1.toString());
            DeleteItemRequest deleteItemRequest2 = new DeleteItemRequest()
                    .withTableName(nToMTableName).withKey(key);
            DeleteItemResult result2 = dynamoDB.deleteItem(deleteItemRequest2);
            Logger.debug("Deleted many-to-many-relationship table item in " + nToMTableName + ": " + result2.toString());
        } catch (AmazonServiceException ase) {
            Logger.error("Deleting many-to-many-relationship item failed.");
            return false;
        }

        return true;
    }

    public boolean deleteAllMRelationships(M obj1) {
        try {
            Map<String, AttributeValue> key1 = dynamoDbMapperHelper.getKey(obj1);
            String key1Name = dynamoDbMapperHelper.getPrimaryHashKeyName(clazzM);
            AttributeValue key1Val = key1.get(key1Name);

            Map<String, Condition> keyConditions = new HashMap<String, Condition>();
            Condition hashKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ.toString())
                    .withAttributeValueList(key1Val);
            keyConditions.put(mHashIdName, hashKeyCondition);
            QueryRequest query = new QueryRequest().withTableName(mToNTableName).withKeyConditions(keyConditions);
            QueryResult res = dynamoDB.query(query);
            for (Map<String, AttributeValue> item : res.getItems()) {
                DeleteItemResult result1 = dynamoDB.deleteItem(new DeleteItemRequest()
                        .withTableName(mToNTableName).withKey(item));
                Logger.trace("Deleted many-to-many-relationship table item in " + mToNTableName + ": " + result1.toString());
                DeleteItemResult result2 = dynamoDB.deleteItem(new DeleteItemRequest()
                        .withTableName(nToMTableName).withKey(item));
                Logger.trace("Deleted many-to-many-relationship table item in " + nToMTableName + ": " + result2.toString());
            }
        } catch (AmazonServiceException ase) {
            Logger.error("Deleting many-to-many-relationship item failed.", ase);
            return false;
        }

        return true;
    }

    public boolean deleteAllNRelationships(N obj2) {
        try {
            Map<String, AttributeValue> key2 = dynamoDbMapperHelper.getKey(obj2);
            String key2Name = dynamoDbMapperHelper.getPrimaryHashKeyName(clazzN);
            AttributeValue key2Val = key2.get(key2Name);

            Map<String, Condition> keyConditions = new HashMap<String, Condition>();
            Condition hashKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ.toString())
                    .withAttributeValueList(key2Val);
            keyConditions.put(nHashIdName, hashKeyCondition);
            QueryRequest query = new QueryRequest().withTableName(nToMTableName).withKeyConditions(keyConditions);
            QueryResult res = dynamoDB.query(query);
            for (Map<String, AttributeValue> item : res.getItems()) {
                DeleteItemResult result1 = dynamoDB.deleteItem(new DeleteItemRequest()
                        .withTableName(mToNTableName).withKey(item));
                Logger.trace("Deleted many-to-many-relationship table item in " + mToNTableName + ": " + result1.toString());
                DeleteItemResult result2 = dynamoDB.deleteItem(new DeleteItemRequest()
                        .withTableName(nToMTableName).withKey(item));
                Logger.trace("Deleted many-to-many-relationship table item in " + nToMTableName + ": " + result2.toString());
            }
        } catch (AmazonServiceException ase) {
            Logger.error("Deleting many-to-many-relationship item failed.", ase);
            return false;
        }

        return true;
    }

    /**
     * @param hashKey The primary hashKey of the relationships
     * @param lastKey The last range key from the previous query.
     * @param limit   Max size of the list that is returned.
     * @return
     */
    protected Integer count(String hashKey, String lastKey, int limit, boolean returnM) {
        // First Query
        QueryResult firstResult = listRelations(hashKey, lastKey, limit, returnM);

        return firstResult.getCount();
    }

    /**
     * @param hashKey The primary hashKey of the relationships
     * @param lastKey The last range key from the previous query.
     * @param limit   Max size of the list that is returned.
     * @return
     */
    protected List<Object> list(String hashKey, String lastKey, int limit, boolean returnM) {
        try {
            // First Query
            QueryResult firstResult = listRelations(hashKey, lastKey, limit, returnM);

            // Second: Load.

            // Prepare the input parameters for the queries
            String targetTableName = returnM ? mTableName : nTableName;
            String objectIdName = dynamoDbMapperHelper.getPrimaryHashKeyName(returnM ? clazzM : clazzN);
            String setterMethodName = "set" + (objectIdName.substring(0, 1)).toUpperCase() + objectIdName.substring(1);
            // Prepare setter method, so we can execute the batchLoad via dynamodbmapper
            Method setterMethod = (returnM ? clazzM : clazzN).getMethod(setterMethodName, String.class);

            // Done preparing the query input parameters.


            // Extract the hashid value from each QueryResult item
            // and put them into objects so that the dynamodbmapper
            // can perform a batchLoad operation.
            List<Object> itemsToGet = new ArrayList<Object>();
            for (Map<String, AttributeValue> item : firstResult.getItems()) {
                // Create new object instance
                Object obj = null;
                if (returnM) {
                    obj = extractObject(item, clazzM, setterMethod, returnM);
                } else {
                    obj = extractObject(item, clazzN, setterMethod, returnM);
                }
                // put the object into the list for batch loading.
                itemsToGet.add(obj);
            }

            List<Object> toReturn = new ArrayList<Object>();
            List<Object> ghosts = new ArrayList<Object>();
            for (Object item : itemsToGet) {
                Object loaded = dynamoDbMapperHelper.load(item);
                if (loaded != null) toReturn.add(loaded);
                else ghosts.add(item);
            }
            if (ghosts.size() > 0) {
                for (Object item : ghosts) {
                    if (returnM) deleteAllMRelationships((M) item);
                    else deleteAllNRelationships((N) item);
                }
                return list(hashKey, lastKey, limit, returnM);
            }

            return toReturn != null ? toReturn : new ArrayList<Object>();

        } catch (InstantiationException e) {
            Logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            Logger.error(e.getMessage());
        } catch (NoSuchMethodException e) {
            Logger.error(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return new ArrayList<Object>();
    }

    private QueryRequest prepareQueryRequest(String hashKey, String lastKey, int limit, boolean returnM) {

        String hashIdName = returnM ? nHashIdName : mHashIdName;
        String rangeIdName = returnM ? mHashIdName : nHashIdName;
        String relationTableName = returnM ? nToMTableName : mToNTableName;

        // Done preparing the query input parameters.

        // Prepare and execute the first DynamoDB Query
        Condition hashKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS(hashKey));

        Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        keyConditions.put(hashIdName, hashKeyCondition);

        Map<String, AttributeValue> exclusiveStartKey = new HashMap<String, AttributeValue>();
        // An AttributeValue may not contain an empty string.


        // Prepare query request.
        QueryRequest queryRequest = new QueryRequest().withTableName(relationTableName)
                .withKeyConditions(keyConditions);

        if (lastKey != null && !lastKey.equals("")) {
            exclusiveStartKey.put(hashIdName, new AttributeValue().withS(hashKey));
            exclusiveStartKey.put(rangeIdName, new AttributeValue().withS(lastKey));

            queryRequest.setExclusiveStartKey(exclusiveStartKey);
            Logger.debug("query with start key: " + exclusiveStartKey);

            /*
            Condition rangeKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.GT.toString())
                    .withAttributeValueList(new AttributeValue().withS(lastKey));
            keyConditions.put(rangeIdName, rangeKeyCondition);
            */
        }
        // Set the limit.
        if (limit > 0)
            queryRequest.setLimit(limit);

        Logger.trace("Table " + relationTableName + " with hash key " + hashKey + " and with range key " + lastKey);

        return queryRequest;
    }

    protected QueryResult listRelations(String hashKey, String lastKey, int limit, boolean returnM) {
        // Prepare the input parameters for the queries
        QueryRequest queryRequest = prepareQueryRequest(hashKey, lastKey, limit, returnM);

        // Query now
        QueryResult result = dynamoDB.query(queryRequest);
        return result;
    }


    private Object extractObject(Map<String, AttributeValue> item, Class clazz, Method setterMethod, boolean returnM) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object obj = clazz.newInstance();
        // Invoke setHashId method
        AttributeValue attrVal = item.get(returnM ? mHashIdName : nHashIdName);
        String hashIdVal = attrVal.getS();
        //Logger.debug("Invoking method " + setterMethodName + " with hash id value " + hashIdVal);
        setterMethod.invoke(obj, hashIdVal);
        return obj;
    }

    public static void main(String[] args) {
        //ManyToManyDAO<Adventure, Category> mToN = new ManyToManyDAO<Adventure, Category>(Adventure.class, Category.class);
        //ManyToManyDAO<Adventure, User> mToN = new ManyToManyDAO<Adventure, User>(Adventure.class, User.class);
        //ManyToManyDAO<Category, Inspiration> mToN = new ManyToManyDAO<Category, Inspiration>(Category.class, Inspiration.class);
        //ManyToManyDAO<Adventure, Inspiration> mToN = new ManyToManyDAO<Adventure, Inspiration>(Adventure.class, Inspiration.class);
        // Create Adventure-Category Many-To-Many Tables
        //mToN.createRelationshipTables();

//        Adventure adv1 = new Adventure();
//        Adventure adv2 = new Adventure();
//        Adventure adv3 = new Adventure();
//        Category catA = new Category();
//        Category catB = new Category();
//
//        new AdventureDAO().save(adv1);
//        new AdventureDAO().save(adv2);
//        new AdventureDAO().save(adv3);
//        new CategoryDAO().save(catA);
//        new CategoryDAO().save(catB);
//
//        mToN.saveManyToManyRelationship(adv1,catA);
//        mToN.saveManyToManyRelationship(adv1,catB);
//        mToN.saveManyToManyRelationship(adv2,catA);
//        mToN.saveManyToManyRelationship(adv3,catA);

//        Adventure adv = new AdventureDAO().get("c1bf3f1c-2ea0-4a96-98fe-cf2ddcd32384");
//        Category cat = new CategoryDAO().get("f9af47cc-42fd-42a6-a40f-930d47999670");
//
//        System.out.println("Adventures of category " + cat.getId());
//        for (Adventure a : mToN.listM(cat.getId(), "0", 10)) {
//            System.out.println(a.getId());
//        }
//
//        mToN.deleteManyToManyRelationship(adv, cat);

//        System.out.println("Categories of adventure "+ adv.getId());
//        for(Category c : mToN.all(adv.getId(),"0",10)) {
//            System.out.println(a.getId());
//        }
    }

    /**
     * Helper method that is only used once for Table setup.
     */
    private void createRelationshipTables() {
        final AmazonDynamoDB dynamoDB = PersistenceHelper.getDynamoDBClient();
        // Create M-to-N relationship Table.
        dynamoDB.createTable(new CreateTableRequest().withTableName(mToNTableName).
                withAttributeDefinitions(new AttributeDefinition().withAttributeName(mHashIdName).withAttributeType("S")).
                withAttributeDefinitions(new AttributeDefinition().withAttributeName(nHashIdName).withAttributeType("S")).
                withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(mHashIdName)).
                withKeySchema(new KeySchemaElement().withKeyType(KeyType.RANGE).withAttributeName(nHashIdName)).
                withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L)));
        // Create N-to-M relationship Table.
        dynamoDB.createTable(new CreateTableRequest().withTableName(nToMTableName).
                withAttributeDefinitions(new AttributeDefinition().withAttributeName(mHashIdName).withAttributeType("S")).
                withAttributeDefinitions(new AttributeDefinition().withAttributeName(nHashIdName).withAttributeType("S")).
                withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(nHashIdName)).
                withKeySchema(new KeySchemaElement().withKeyType(KeyType.RANGE).withAttributeName(mHashIdName)).
                withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L)));

    }

}
