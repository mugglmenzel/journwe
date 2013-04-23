package controllers.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import models.Comment;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import controllers.dao.common.CommonRangeEntityDAO;

public class CommentDAO extends CommonRangeEntityDAO<Comment> {
	
	private static String TIMESTAMP_INDEX_NAME = "timestampIndex";
	private static String TIMESTAMP_ATTRIBUTE = "timestamp";

	public CommentDAO() {
		super(Comment.class);
	}

	/**
	 * Get an iterator of comments, queried by the thread id.
	 */
	public Iterator<Comment> getCommentsByThreadId(String threadId) {
		DynamoDBScanExpression scan = new DynamoDBScanExpression();
		scan.addFilterCondition("commentThreadId", new Condition()
				.withAttributeValueList(new AttributeValue(threadId))
				.withComparisonOperator(ComparisonOperator.EQ));
		PaginatedScanList<Comment> result = pm.scan(Comment.class, scan);
		return result.iterator();
	}
	
	/**
	 * Get an iterator of comments, queried by the timestamp.
	 */
	public Iterator<Comment> getCommentsByTimestamp() {
		DynamoDBQueryExpression<Comment> qe = new DynamoDBQueryExpression<Comment>();
		HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
		qe.setIndexName(TIMESTAMP_INDEX_NAME);
		String currentTime = ""+new Date().getTime(); 
		keyConditions.put(
				TIMESTAMP_ATTRIBUTE,
				new Condition().withComparisonOperator(
						ComparisonOperator.LT).withAttributeValueList(
						new AttributeValue().withN(currentTime)));
		qe.setRangeKeyConditions(keyConditions);
		PaginatedQueryList<Comment> result = pm.query(Comment.class, qe);
		return result.iterator();
	}

}
