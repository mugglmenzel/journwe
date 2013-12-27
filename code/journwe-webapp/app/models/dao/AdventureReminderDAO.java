package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.AdventureReminder;
import models.adventure.EAdventureReminderType;
import models.dao.common.CommonEntityDAO;
import models.dao.common.CommonRangeEntityDAO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 26.12.13
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */
public class AdventureReminderDAO extends CommonRangeEntityDAO<AdventureReminder> {

    public AdventureReminderDAO() {
        super(AdventureReminder.class);
    }


    public List<AdventureReminder> all(EAdventureReminderType type) {
        AdventureReminder rem = new AdventureReminder();
        rem.setType(type);

        return pm.query(AdventureReminder.class, new DynamoDBQueryExpression().withHashKeyValues(rem));
    }


    public List<AdventureReminder> allFromPast(EAdventureReminderType type, Long pastTime) {
        AdventureReminder rem = new AdventureReminder();
        rem.setType(type);

        return pm.query(AdventureReminder.class, new DynamoDBQueryExpression().withHashKeyValues(rem).withRangeKeyCondition("reminderDate", new Condition().withComparisonOperator(ComparisonOperator.GE).withAttributeValueList(new AttributeValue().withN(pastTime.toString()))).withIndexName("reminder-index"));
    }

    public List<AdventureReminder> allFromPastToNow(EAdventureReminderType type, Long pastTime) {
        AdventureReminder rem = new AdventureReminder();
        rem.setType(type);

        Map<String,Condition> cond = new HashMap<String, Condition>();
        cond.put("reminderDate", new Condition().withComparisonOperator(ComparisonOperator.LE).withAttributeValueList(new AttributeValue().withN(new Long(new Date().getTime()).toString())));
        cond.put("reminderDate", new Condition().withComparisonOperator(ComparisonOperator.GE).withAttributeValueList(new AttributeValue().withN(pastTime.toString())));

        return pm.query(AdventureReminder.class, new DynamoDBQueryExpression().withHashKeyValues(rem).withRangeKeyConditions(cond).withIndexName("reminder-index"));
    }

}
