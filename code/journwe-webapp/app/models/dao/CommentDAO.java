package models.dao;

import java.util.List;

import models.Comment;

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
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("threadId", new Condition().withAttributeValueList(new AttributeValue(threadId)).withComparisonOperator(ComparisonOperator.EQ));
        PaginatedScanList<Comment> scanResult = pm.scan(Comment.class, scan);
        return scanResult;
	}
	
}
