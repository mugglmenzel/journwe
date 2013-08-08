package models.adventure;

import models.user.User;
import models.user.UserSocial;
import models.dao.UserDAO;
import models.dao.UserSocialDAO;
import play.data.validation.Constraints.Required;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * The comment model with range key = threadId and secondary index = timestamp.
 *
 * @author markus
 */
@DynamoDBTable(tableName = "journwe-comment")
public class Comment {

    @Required
    private String threadId;

    private Long timestamp;

    private String userId;

    @Required
    private String text;


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
