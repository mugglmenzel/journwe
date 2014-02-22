package models.dao.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.dao.common.CommonRangeEntityDAO;
import models.user.UserEmail;

import java.util.Iterator;
import java.util.List;

public class UserEmailDAO extends CommonRangeEntityDAO<UserEmail> {

    public UserEmailDAO() {
        super(UserEmail.class);
    }


    public List<UserEmail> getEmailsOfUser(String userId) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        UserEmail ue = new UserEmail();
        ue.setUserId(userId);
        query.setHashKeyValues(ue);

        return pm.query(clazz, query);
    }

    public UserEmail getPrimaryEmailOfUser(String userId) {
        Iterator<UserEmail> results = getEmailsOfUser(userId).iterator();
        UserEmail result = null;
        while(results.hasNext()) {
            result = results.next();
            if(result.isPrimary()) return result;
        }

        if(result != null) {
            result.setPrimary(true);
            save(result);
        }

        return result;
    }


}
