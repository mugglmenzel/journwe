package models.notifications;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 13.02.14
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-notification-adventure")
public class AdventureNotification {

    private String adventureId;

    private Long timestamp;

    private String topic;

    private String section;

    private String data;

}
