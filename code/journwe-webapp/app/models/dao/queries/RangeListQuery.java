package models.dao.queries;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.PersistenceHelper;
import models.dao.helpers.DynamoDBMapperHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by markus on 15/01/14.
 */
public class RangeListQuery<T> extends Query<T> {

    public RangeListQuery(Class<T> clazz) {
        super(clazz);
    }

    public List<T> listBy(String hashKey, String lastRangeKey, int limit) {
        DynamoDBQueryExpression<T> queryExpression = new DynamoDBQueryExpression<T>();
        try {
            T hashKeyObject = null;
            hashKeyObject = clazz.newInstance();
            setHashKeyMethod.invoke(hashKeyObject, hashKey);
            queryExpression.setHashKeyValues(hashKeyObject);
            if (lastRangeKey != null && !"".equals(lastRangeKey)) {
                Map<String, Condition> rangeKeyConditions = new HashMap<String, Condition>();
                Condition rangeKeyCondition = new Condition()
                        .withComparisonOperator(ComparisonOperator.GT.toString())
                        .withAttributeValueList(new AttributeValue().withS(lastRangeKey));
                rangeKeyConditions.put(rangeKeyName, rangeKeyCondition);
                queryExpression.setRangeKeyConditions(rangeKeyConditions);
            }
            queryExpression.setLimit(limit);

            List<T> results = pm.query(clazz, queryExpression);
            return results.subList(0, results.size() >= limit ? limit : results.size());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new ArrayList<T>();
    }

    public List<T> all(String hashKey) {
        try {
            T hashKeyObject = clazz.newInstance();
            setHashKeyMethod.invoke(hashKeyObject, hashKey);
            DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(hashKey);
            return pm.query(clazz, query);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return new ArrayList<T>();
    }

}
