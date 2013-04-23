package controllers.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import controllers.dao.common.CommonRangeEntityDAO;
import models.Adventurer;

import java.util.Iterator;
import java.util.List;

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

    public Iterator<Adventurer> all(String advId) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(new AttributeValue(advId)).withConsistentRead(true);
        return pm.query(Adventurer.class, query).iterator();

        //DynamoDBScanExpression scan = new DynamoDBScanExpression();
        //scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
        //return pm.scan(Adventurer.class, scan).iterator();
    }

    public List<Adventurer> all(int max, String advId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(max);
        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.scan(Adventurer.class, scan);
    }
}
