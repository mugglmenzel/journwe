package controllers.dao;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import controllers.dao.common.CommonEntityDAO;
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
public class AdventurerDAO extends CommonEntityDAO<Adventurer> {

    public AdventurerDAO() {
        super(Adventurer.class);
    }

    public Iterator<Adventurer> findByAdventureId(String advId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.scan(Adventurer.class, scan).iterator();
    }

    public List<Adventurer> all(int max, String advId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(max);
        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.scanPage(Adventurer.class, scan).getResults();
    }
}
