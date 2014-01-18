package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.Adventure;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.dao.common.AdventureComponentDAO;
import models.dao.common.CommonEntityDAO;
import models.dao.common.CommonRangeEntityDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 22.06.13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class PlaceOptionDAO extends AdventureComponentDAO<PlaceOption> {


    public PlaceOptionDAO() {
        super(PlaceOption.class);
    }

}
