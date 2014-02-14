package com.journwe.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 20.11.13
 * Time: 22:23
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-email-message")
public class Message {

    private String adventureId;

    private Long timestamp;

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
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    @DynamoDBIgnore
    public String getMessageId() {
        return getAdventureId() + "-" + getTimestamp();
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

    @Override
    public String toString() {
        return "Message{" +
                "adventureId='" + adventureId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sender='" + sender + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", attachments=" + attachments +
                '}';
    }

}
