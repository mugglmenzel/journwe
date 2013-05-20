package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.UserEmail;
import models.dao.common.CommonRangeEntityDAO;

import java.util.List;

public class UserEmailDAO extends CommonRangeEntityDAO<UserEmail> {

    public UserEmailDAO() {
        super(UserEmail.class);
    }

    public UserEmail getPrimaryEmailOfUser(String userId) {
       DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("userId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(userId)));
        scan.addFilterCondition("primary", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue("true")));

        List<UserEmail> result = pm.scan(clazz, scan);

        return result.size() > 0 ? result.get(0) : null;

    }
}
