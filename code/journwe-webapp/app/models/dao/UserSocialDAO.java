package models.dao;

import models.UserSocial;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import models.dao.common.CommonRangeEntityDAO;

public class UserSocialDAO extends CommonRangeEntityDAO<UserSocial> {

    public UserSocialDAO() {
        super(UserSocial.class);
    }

    public UserSocial findBySocialId(String socialId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("socialId", new Condition().withAttributeValueList(new AttributeValue(socialId)).withComparisonOperator(ComparisonOperator.EQ));
        PaginatedScanList<UserSocial> result = pm.scan(UserSocial.class, scan);
        return !result.isEmpty() ? result.get(0) : null;
    }

}
