package controllers.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import controllers.dao.common.CommonEntityDAO;
import controllers.dao.common.CommonRangeEntityDAO;
import models.Adventurer;
import models.Inspiration;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
    	// TODO
//        DynamoDBQueryExpression query = new DynamoDBQueryExpression(new AttributeValue(advId)).withConsistentRead(true);
//        return pm.query(Adventurer.class, query).iterator();
    	return null;
        
        //DynamoDBScanExpression scan = new DynamoDBScanExpression();
        //scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
        //return pm.scan(Adventurer.class, scan).iterator();
    }

    public List<Adventurer> all(int max, String advId) {
    	// TODO
//        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(max);
//        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
//        return pm.scanPage(Adventurer.class, scan).getResults();
    	return null;
    }
}
