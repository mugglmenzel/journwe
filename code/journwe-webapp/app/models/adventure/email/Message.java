package models.adventure.email;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 20.11.13
 * Time: 22:23
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-james-adventuremail")
public class Message {

    private String adventureId;

    private String timestamp;

    private String sender;

    private String subject;

    private String body;

    private Set attachments;

    @DynamoDBHashKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBRangeKey
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @DynamoDBAttribute
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @DynamoDBAttribute
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @DynamoDBAttribute
    public Set getAttachments() {
        return attachments;
    }

    public void setAttachments(Set attachments) {
        this.attachments = attachments;
    }
}
