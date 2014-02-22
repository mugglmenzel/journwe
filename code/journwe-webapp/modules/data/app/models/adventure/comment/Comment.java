package models.adventure.comment;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.adventure.IAdventureComponentWithUser;
import play.data.validation.Constraints.Required;

/**
 * The comment model with range key = threadId and secondary index = timestamp.
 *
 * @author markus
 */
@DynamoDBTable(tableName = "journwe-comment")
public class Comment implements IAdventureComponentWithUser {

    @Required
    private String threadId;

    private Long timestamp;

    private String userId;

    @Required
    private String text;

    @DynamoDBIgnore
    public String getAdventureId() {
        return threadId.indexOf("_") > -1 ? threadId.substring(0, threadId.indexOf("_")) : null;
    }

    public void setAdventureId(String adventureId) {
        threadId = adventureId + "" + (threadId.indexOf("_") > -1 ? threadId.substring(threadId.indexOf("_")) : "");
    }

    @DynamoDBHashKey(attributeName = "threadId")
    @DynamoDBAttribute
    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @DynamoDBRangeKey(attributeName = "timestamp")
    @DynamoDBAttribute
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long time) {
        this.timestamp = time;
    }

    @DynamoDBAttribute
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


//    @DynamoDBIndexRangeKey(localSecondaryIndexName = "timestamp-index", attributeName = "timestamp")
//    @DynamoDBAttribute
//    public Long getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(Long time) {
//        this.timestamp = time;
//    }

    @DynamoDBAttribute
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
