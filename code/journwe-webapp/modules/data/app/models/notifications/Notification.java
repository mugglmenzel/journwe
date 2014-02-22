package models.notifications;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.dao.helpers.EnumMarshaller;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 30.07.13
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-notification")
public class Notification {

    private String userId;

    private Date created = new Date();

    private ENotificationTopics topic;

    private String topicRef;

    private String subject;

    private String message;

    private boolean read = false;

    private boolean sent = false;

    @DynamoDBHashKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @DynamoDBMarshalling(marshallerClass = NotificationTopicMarshaller.class)
    public ENotificationTopics getTopic() {
        return topic;
    }

    public void setTopic(ENotificationTopics topic) {
        this.topic = topic;
    }

    @DynamoDBAttribute
    public String getTopicRef() {
        return topicRef;
    }

    public void setTopicRef(String topicRef) {
        this.topicRef = topicRef;
    }

    @DynamoDBAttribute
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @DynamoDBAttribute
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @DynamoDBAttribute
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @DynamoDBAttribute
    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public static class NotificationTopicMarshaller extends EnumMarshaller<ENotificationTopics> {
    }

}
