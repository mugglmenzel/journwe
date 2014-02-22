package models.notifications;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 31.07.13
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-notification-digestqueue-item")
public class NotificationDigestQueueItem {

    private String digestQueueName;

    private String userId;

    @DynamoDBHashKey
    public String getDigestQueueName() {
        return digestQueueName;
    }

    public void setDigestQueueName(String digestQueueName) {
        this.digestQueueName = digestQueueName;
    }

    @DynamoDBRangeKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
