package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.dao.common.CommonEntityDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 22.06.13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class PlaceOptionDAO extends CommonEntityDAO<PlaceOption> {


    public PlaceOptionDAO() {
        super(PlaceOption.class);
    }

    public List<PlaceOption> all(String advId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.scan(PlaceOption.class, scan);
    }

}
