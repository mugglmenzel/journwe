package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.user.UserEmail;
import models.dao.common.CommonRangeEntityDAO;

import java.util.Iterator;
import java.util.List;

public class UserEmailDAO extends CommonRangeEntityDAO<UserEmail> {

    public UserEmailDAO() {
        super(UserEmail.class);
    }

    public UserEmail getPrimaryEmailOfUser(String userId) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        UserEmail ue = new UserEmail();
        ue.setUserId(userId);
        ue.setPrimary(true);
        query.setHashKeyValues(ue);

        Iterator<UserEmail> results = pm.query(clazz, query).iterator();
        UserEmail result = null;
        while(results.hasNext()) {
            result = results.next();
            if(result.isPrimary()) return result;
        }
        return result;
    }
}
