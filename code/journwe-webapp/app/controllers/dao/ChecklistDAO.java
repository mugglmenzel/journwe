package controllers.dao;

import java.util.Iterator;

import models.Checklist;
import models.CommentThread;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import controllers.dao.common.CommonEntityDAO;
import controllers.dao.common.ITopicDAO;

public class ChecklistDAO extends CommonEntityDAO<Checklist> implements ITopicDAO<Checklist> {

    public ChecklistDAO() {
        super(Checklist.class);
    }

	@Override
	public Iterator<CommentThread> getThreads(String adventureId, String checklistId) {
		DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(new AttributeValue(adventureId)).withRangeKeyCondition("checklistId", new Condition().withComparisonOperator(
						ComparisonOperator.EQ).withAttributeValueList(
						new AttributeValue().withS(checklistId))).withConsistentRead(false);
        return pm.query(CommentThread.class, query).iterator();
	}

}
