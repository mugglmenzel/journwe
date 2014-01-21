package models.dao.queries;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.PersistenceHelper;
import models.dao.helpers.DynamoDBMapperHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by markus on 15/01/14.
 */
public class RangeCountQuery<T> extends Query<T> {

    public RangeCountQuery(Class<T> clazz) {
        super(clazz);
    }

    public int count(String hashKey) {
        try {
        T hashKeyObject = clazz.newInstance();
        setHashKeyMethod.invoke(hashKeyObject, hashKey);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(hashKeyObject);
        return pm.count(clazz, query);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
