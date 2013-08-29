package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.dao.common.CommonEntityDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdventureDAO extends CommonEntityDAO<Adventure> {

    public AdventureDAO() {
        super(Adventure.class);
    }

    public List<Adventure> allOfUserId(String userId) {
        List<Adventure> results = new ArrayList<Adventure>();
        for (Adventurer avr : new AdventurerDAO().allOfUserId(userId)) {
            results.add(get(avr.getAdventureId()));
        }
        return results;
    }

    public int countOfUserId(String userId) {
        return new AdventurerDAO().countOfUserId(userId);
    }

    public List<Adventure> allOfUserId(String userId, String lastKey, int limit) {
        List<Adventure> results = new ArrayList<Adventure>();
        for (Adventurer avr : new AdventurerDAO().allOfUserId(userId, lastKey, limit)) {
            results.add(get(avr.getAdventureId()));
        }
        return results.subList(0, results.size() >= limit ? limit : results.size());
    }

    public List<Adventure> all() {
        return pm.scan(clazz,
                new DynamoDBScanExpression());
    }


    public List<Adventure> allPublic() {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("publish", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withN("1")));
        return pm.scan(clazz,
                scan);
    }

    public List<Adventure> allPublic(String lastKey, int limit) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(limit);
        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("id", new AttributeValue(lastKey));
            scan.setExclusiveStartKey(startkey);
        }
        scan.addFilterCondition("publish", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withN("1")));
        List<Adventure> results = pm.scan(clazz,
                scan);
        return results.subList(0, results.size() >= limit ? limit : results.size());
    }


}
