package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.checklist.Todo;
import models.adventure.time.TimeOption;
import models.dao.common.AdventureComponentDAO;
import models.dao.common.CommonEntityDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 04.06.13
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class TimeOptionDAO extends AdventureComponentDAO<TimeOption> {

    public TimeOptionDAO() {
        super(TimeOption.class);
    }

}
