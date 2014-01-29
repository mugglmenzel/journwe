package models.adventure.email;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 28.01.14
 * Time: 09:28
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-email-attachment")
public class MessageAttachment {

    private String messageId;

    private String fileName;

    @DynamoDBHashKey
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @DynamoDBRangeKey
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
