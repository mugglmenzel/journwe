package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.dao.common.CommonEntityDAO;

import java.util.ArrayList;
import java.util.List;

public class AdventureDAO extends CommonEntityDAO<Adventure> {

    public AdventureDAO() {
        super(Adventure.class);
    }

    public List<Adventure> allOfUserId(String userId) {
        List<Adventure> result = new ArrayList<Adventure>();
        for (Adventurer avr : new AdventurerDAO().allOfUserId(userId)) {
            result.add(get(avr.getAdventureId()));
        }
        return result;
    }

    public List<Adventure> all() {
        return pm.scan(Adventure.class,
                new DynamoDBScanExpression());
    }


    public List<Adventure> allPublic() {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("publish", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withN("1")));
        return pm.scan(Adventure.class,
                scan);
    }


}
