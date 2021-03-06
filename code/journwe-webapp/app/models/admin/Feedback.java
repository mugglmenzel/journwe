package models.admin;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.dao.helpers.EnumMarshaller;

import java.util.Date;

@DynamoDBTable(tableName = "journwe-admin-feedback")
public class Feedback {

    private String id;
    private String userId;
    private String userName;
    private Date timeStamp;
    private String text;
    private EFeedbackType feedbackType;

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @DynamoDBAttribute
    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @DynamoDBAttribute
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @DynamoDBMarshalling(marshallerClass = FeedbackTypeMarshaller.class)
    public EFeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(EFeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public static class FeedbackTypeMarshaller extends EnumMarshaller<EFeedbackType> {
    }

}
