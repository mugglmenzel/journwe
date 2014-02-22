package models.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.dao.helpers.EnumMarshaller;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 26.12.13
 * Time: 08:59
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-adventure-reminder")
public class AdventureReminder {

    private EAdventureReminderType type = EAdventureReminderType.PLACE;

    private String adventureId;

    private Long reminderDate;


    @DynamoDBHashKey
    @DynamoDBMarshalling(marshallerClass = ReminderTypeMarshaller.class)
    public EAdventureReminderType getType() {
        return type;
    }

    public void setType(EAdventureReminderType type) {
        this.type = type;
    }

    @DynamoDBRangeKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBIndexRangeKey(localSecondaryIndexName = "reminder-index")
    public Long getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(Long reminderDate) {
        this.reminderDate = reminderDate;
    }

    public static class ReminderTypeMarshaller extends EnumMarshaller<EAdventureReminderType> {
    }
}
