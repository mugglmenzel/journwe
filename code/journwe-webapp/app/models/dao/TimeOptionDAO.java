package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.checklist.Todo;
import models.adventure.time.TimeOption;
import models.dao.common.CommonEntityDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 04.06.13
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class TimeOptionDAO extends CommonEntityDAO<TimeOption> {

    public TimeOptionDAO() {
        super(TimeOption.class);
    }


    public List<TimeOption> all(String advId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.scan(TimeOption.class, scan);
    }

}
