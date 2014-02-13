package models.dao.adventure;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.comment.Comment;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import models.dao.common.CommonRangeEntityDAO;

public class CommentDAO extends CommonRangeEntityDAO<Comment> {
	
    public CommentDAO() {
        super(Comment.class);
    }
    
	/**
	 * Get the list of comments that belong to the thread, sorted by timestamp.
	 */
	public List<Comment> getComments(final String threadId) {
        Comment hashKey = new Comment();
        hashKey.setThreadId(threadId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(hashKey);
        return pm.query(Comment.class, query);
	}
	
}
