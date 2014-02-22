package models.dao.queries;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import models.dao.common.PersistenceHelper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by markus on 18/01/14.
 */
public class GSIQuery {

    private String tableName;
    private String indexName;
    private String hashKeyName;

    public GSIQuery(final String tableName, final String indexName, final String hashKeyName) {
        this.tableName = tableName;
        this.indexName = indexName;
        this.hashKeyName = hashKeyName;
    }

    public QueryResult query(final String hashKey) {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withIndexName(indexName);
        HashMap<String, Condition> keyConditions =
                new HashMap<String, Condition>();
        keyConditions.put(hashKeyName, new Condition().
                withComparisonOperator(
                        ComparisonOperator.EQ).withAttributeValueList(
                new AttributeValue().withS(hashKey)));
        queryRequest.setKeyConditions(keyConditions);
        queryRequest.setSelect(Select.ALL_PROJECTED_ATTRIBUTES);
        AmazonDynamoDB dynamoDbClient = PersistenceHelper.
                getDynamoDBClient();
        QueryResult queryResult = dynamoDbClient.
                query(queryRequest);
        return queryResult;
    }

}
