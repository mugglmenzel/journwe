package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.Adventure;
import models.dao.common.CommonRangeEntityDAO;
import models.adventure.Adventurer;
import play.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 15.04.13
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class AdventurerDAO extends CommonRangeEntityDAO<Adventurer> {

    public AdventurerDAO() {
        super(Adventurer.class);
    }

    public Adventurer getAdventurerByUserId(String userId) {
        return null;
    }

    public boolean isAdventurer(String userId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("userId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(userId)));
        return pm.count(clazz, scan) > 0;
    }

    public List<Adventurer> allOfUserId(String userId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("userId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(userId)));
        return pm.scan(clazz, scan);
    }

    public int countOfUserId(String userId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("userId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(userId)));
        return pm.count(clazz, scan);
    }

    public List<Adventurer> allOfUserId(String userId, String lastKey, int limit) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(limit);
        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("adventureId", new AttributeValue(lastKey));
            startkey.put("userId", new AttributeValue(userId));
            scan.setExclusiveStartKey(startkey);
        }
        scan.addFilterCondition("userId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(userId)));

        List<Adventurer> results = pm.scan(clazz, scan);
        return results.subList(0, results.size() >= limit ? limit : results.size());
    }

    public List<Adventurer> all(String advId) {
        Adventurer hashKeyObj = new Adventurer();
        hashKeyObj.setAdventureId(advId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(hashKeyObj);

        return pm.query(Adventurer.class, query);
    }

    public int count(String advId) {
        Adventurer hashKeyObj = new Adventurer();
        hashKeyObj.setAdventureId(advId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(hashKeyObj);

        return pm.count(Adventurer.class, query);
    }
}
