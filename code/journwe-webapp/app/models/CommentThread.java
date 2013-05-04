package models;

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
	
	// this is the id of the object next to which the comment thread is shown
	@Required
	private String topicId;
	
	private String threadId;

	@DynamoDBHashKey(attributeName = "adventureId")
	@DynamoDBAttribute
	public String getAdventureId() {
		return adventureId;
	}

	public void setAdventureId(String adventureId) {
		this.adventureId = adventureId;
	}

	@DynamoDBRangeKey(attributeName = "topicId")
	@DynamoDBAttribute
	public String getTopicId() {
		return topicId;
	}

	// the topic id is the original object id which is prefixed with the type of the topic/object
	public void setTopicId(String topicType, String objectId) {
		this.topicId = topicType+objectId;
	}
	
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	@DynamoDBAttribute
	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public void setThreadId(String advId, String topicId) {
		this.threadId = advId+topicId;
	}
    
}
