package models.adventure;

import play.data.validation.Constraints.Required;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Each adventure can have a couple of comment threads for discussion among the
 * adventurers. Any thread must be associated with a topic, which can be any
 * object in a given adventure that implements the <code>ITopicDAO</code>
 * interface. The topicId is equal to the object's id.
 * 
 * @author markus
 */
@DynamoDBTable(tableName = "journwe-commentthread")
public class CommentThread {

	@Required
	private String adventureId;
	
	@Required
	private String topicType;
	
	private String threadId;

	@DynamoDBHashKey(attributeName = "adventureId")
	@DynamoDBAttribute
	public String getAdventureId() {
		return adventureId;
	}

	public void setAdventureId(String adventureId) {
		this.adventureId = adventureId;
	}

	@DynamoDBRangeKey(attributeName = "topicType")
	@DynamoDBAttribute
	public String getTopicType() {
		return topicType;
	}

	public void setTopicType(String topicType) {
		this.topicType = topicType;
	}
	
	@DynamoDBAttribute
	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public void setThreadId(String advId, String topicType) {
		setThreadId(advId+topicType);
	}
    
}
