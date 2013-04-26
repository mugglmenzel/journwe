package controllers.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import models.Comment;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import controllers.dao.common.PersistenceHelper;

//public class CommentDAO extends CommonRangeEntityDAO<Comment> {

public class CommentDAO {
	
	private static String TIMESTAMP_INDEX_NAME = "timestamp-index";
	private static String TIMESTAMP_ATTRIBUTE = "timestamp";
	
//    public CommentDAO() {
//        super(Comment.class);
//    }

	protected static DynamoDBMapper pm = PersistenceHelper.getManager();
	
	public CommentDAO() {
		
	}

//	/**
//	 * Get an iterator of comments, queried by the thread id.
//	 */
//	public Iterator<Comment> getCommentsByThreadId(String threadId) {
//		DynamoDBScanExpression scan = new DynamoDBScanExpression();
//		scan.addFilterCondition("commentThreadId", new Condition()
//				.withAttributeValueList(new AttributeValue(threadId))
//				.withComparisonOperator(ComparisonOperator.EQ));
//		PaginatedScanList<Comment> result = pm.scan(Comment.class, scan);
//		if(result!=null)	{
//			return result.iterator();
//		} else {
//			return null;
//		}
//	}
	
//	/**
//	 * Get an iterator of comments, queried by the timestamp.
//	 */
//	public Iterator<Comment> getCommentsByTimestamp() {
//		DynamoDBQueryExpression<Comment> qe = new DynamoDBQueryExpression<Comment>();
//		HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
//		qe.setIndexName(TIMESTAMP_INDEX_NAME);
//		String currentTime = ""+new Date().getTime();
//		// Set range key condition TODO
//		keyConditions.put("commentThreadId", new Condition().withComparisonOperator(
//				ComparisonOperator.EQ).withAttributeValueList(
//				new AttributeValue().withS("1")));
//		// Set range key index condition
//		keyConditions.put(
//				TIMESTAMP_ATTRIBUTE,
//				new Condition().withComparisonOperator(
//						ComparisonOperator.LT).withAttributeValueList(
//						new AttributeValue().withN(currentTime)));
//		qe.setRangeKeyConditions(keyConditions);
//		qe.setConsistentRead(false);
//		qe.setScanIndexForward(true);
//		PaginatedQueryList<Comment> result = pm.query(Comment.class, qe);
//		if(result != null)	{
//			return result.iterator();
//		} else {
//			return null;
//		}
//	}

	/**
	 * Get an iterator of comments, queried by the timestamp.
	 */
	public Iterator<Comment> getCommentsByTimestamp() {
		DynamoDBQueryExpression<Comment> qe = new DynamoDBQueryExpression<Comment>();
		HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
		qe.setIndexName(TIMESTAMP_INDEX_NAME);
		String currentTime = ""+new Date().getTime();
		// Set range key condition TODO
//		keyConditions.put("commentThreadId", new Condition().withComparisonOperator(
//				ComparisonOperator.EQ).withAttributeValueList(
//				new AttributeValue().withS("1")));
		// Set range key index condition
		keyConditions.put(
				TIMESTAMP_ATTRIBUTE,
				new Condition().withComparisonOperator(
						ComparisonOperator.LT).withAttributeValueList(
						new AttributeValue().withN(currentTime)));
		qe.setRangeKeyConditions(keyConditions);
		qe.setConsistentRead(false);
//		qe.setScanIndexForward(true);
		qe.setHashKeyValues(new Comment());
		PaginatedQueryList<Comment> result = pm.query(Comment.class, qe);
		if(result != null)	{
			return result.iterator();
		} else {
			return null;
		}
	}
	
//	/**
//	 * Get an iterator of comments, queried by the timestamp.
//	 */
//	public Iterator<Comment> getCommentsByTimestamp() {
//		AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials(ConfigFactory
//				.load().getString("aws.accessKey"), ConfigFactory.load()
//				.getString("aws.secretKey")));
//		
//		HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
//		// Set range key index condition
//		String currentTime = ""+new Date().getTime();
//		keyConditions.put(
//				TIMESTAMP_ATTRIBUTE,
//				new Condition().withComparisonOperator(
//						ComparisonOperator.LT).withAttributeValueList(
//						new AttributeValue().withN(currentTime)));
//		
//		QueryRequest req = new QueryRequest().withTableName("journwe-comment")
//				.withConsistentRead(false).withIndexName(TIMESTAMP_INDEX_NAME).withKeyConditions(keyConditions);
//		
//		
//		QueryResult result = client.query(req);
//		List<Map<String, AttributeValue>> items = result.getItems();
//		Iterator<Map<String, AttributeValue>> itemsIter = items.iterator();
//		while (itemsIter.hasNext()) {
//			Map<String, AttributeValue> currentItem = itemsIter.next();
//			
//			Iterator<String> currentItemIter = currentItem.keySet().iterator();
//			while (currentItemIter.hasNext()) {
//				String attr = (String) currentItemIter.next();
//				if (attr == "timestamp") {
//					Logger.info(attr + "---> "
//							+ currentItem.get(attr).getN());
//				} else {
//					Logger.info(attr + "---> "
//							+ currentItem.get(attr).getS());
//				}
//			}
//		}
//		
//		return null;
//	}
	
}
