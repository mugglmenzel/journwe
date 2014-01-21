package models.dao.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.dao.common.CommonEntityDAO;
import models.user.UserEmail;
import java.util.Iterator;

public class UserEmailDAO extends CommonEntityDAO<UserEmail> {

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
