package models.dao.queries;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import models.dao.common.PersistenceHelper;
import models.dao.helpers.DynamoDBMapperHelper;

import java.lang.reflect.Method;

/**
 * Created by markus on 15/01/14.
 */
public class Query<T> {

    static DynamoDBMapperHelper dynamoDbMapperHelper = new DynamoDBMapperHelper();
    DynamoDBMapper pm = PersistenceHelper.getManager();
    Class<T> clazz;
    String hashKeyName;
    String rangeKeyName;
    Method setHashKeyMethod;

    protected Query(Class<T> clazz) {
        this.clazz = clazz;
        init();
    }

    /**
     * Load hash key name and other parameters by using Java reflection.
     */
    protected void init() {
        try {
            hashKeyName = dynamoDbMapperHelper.getPrimaryHashKeyName(clazz);
            rangeKeyName = dynamoDbMapperHelper.getPrimaryRangeKeyName(clazz);
            String setterMethodName = "set" + (hashKeyName.substring(0, 1)).toUpperCase() + hashKeyName.substring(1);
            setHashKeyMethod = clazz.getMethod(setterMethodName, String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
