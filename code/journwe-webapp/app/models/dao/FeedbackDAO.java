package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.admin.Feedback;
import models.adventure.CommentThread;
import models.dao.common.AdventureComponentDAO;
import models.dao.common.CommonRangeEntityDAO;

import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO  extends CommonRangeEntityDAO<Feedback> {

	public FeedbackDAO() {
		super(Feedback.class);
	}

    public List<Feedback> all(final String feedbackid) {
        DynamoDBQueryExpression<Feedback> qe = getQueryExpression(feedbackid);
        List<Feedback> result = pm.query(clazz, qe);
        if(result != null)	{
            // return the results
            return result;
        } else {
            // ... else: return an empty list
            return new ArrayList<Feedback>();
        }
    }

    protected DynamoDBQueryExpression<Feedback> getQueryExpression(final String feedbackid) {
        Feedback key = new Feedback();
        key.setId(feedbackid);
        // Hash key = adventure id
        DynamoDBQueryExpression<Feedback> qe = new DynamoDBQueryExpression<Feedback>().withHashKeyValues(key);
        return qe;
    }

}
