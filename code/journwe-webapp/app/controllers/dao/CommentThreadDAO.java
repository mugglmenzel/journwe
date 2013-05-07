package controllers.dao;

import java.util.ArrayList;
import java.util.List;

import models.CommentThread;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

import controllers.dao.common.CommonRangeEntityDAO;

public class CommentThreadDAO<T> extends CommonRangeEntityDAO<CommentThread> {
	
	public CommentThreadDAO() {
		super(CommentThread.class);
	}

	/**
	 * Get the list of comment threads that belong to the adventure.
	 */
	public List<CommentThread> getCommentThreads(final String adventureId) {
		CommentThread commentThreadKey = new CommentThread();
		commentThreadKey.setAdventureId(adventureId);
		// Hash key = adventure id
		DynamoDBQueryExpression<CommentThread> qe = new DynamoDBQueryExpression<CommentThread>().withHashKeyValues(commentThreadKey);
//		Map<String, Condition> rangeKeyConditions = new HashMap<String,Condition>();
//		// Range key = topic type + object id
//		rangeKeyConditions.put("topicId", new Condition().withComparisonOperator(
//				ComparisonOperator.EQ).withAttributeValueList(
//				new AttributeValue().withS(topicType+objectId)));
//		qe.setRangeKeyConditions(rangeKeyConditions );
		PaginatedQueryList<CommentThread> result = pm.query(CommentThread.class, qe);
		if(result != null)	{
			// return the results
			return result;
		} else {
			// ... else: return an empty list
			return new ArrayList<CommentThread>();
		}
	}

}
