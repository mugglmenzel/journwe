package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.Adventure;
import models.adventure.AdventureShortname;
import models.dao.common.CommonEntityDAO;

public class AdventureShortnameDAO extends CommonEntityDAO<AdventureShortname> {

    public AdventureShortnameDAO() {
        super(AdventureShortname.class);
    }

    public AdventureShortname getShortname(String advId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("adventureId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(advId)));
        return pm.scan(AdventureShortname.class, scan).get(0);
    }

    public Adventure getAdventureByShortname(String shortname) {
        return new AdventureDAO().get(get(shortname).getAdventureId());
    }

    public boolean exists(String shortname) {
        return get(shortname) != null;
    }
}
