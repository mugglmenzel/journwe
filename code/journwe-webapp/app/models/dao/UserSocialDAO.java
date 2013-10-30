package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.feth.play.module.pa.user.AuthUser;
import models.dao.common.CommonRangeEntityDAO;
import models.user.User;
import models.user.UserSocial;
import play.Logger;

import java.util.List;

public class UserSocialDAO extends CommonRangeEntityDAO<UserSocial> {

    public UserSocialDAO() {
        super(UserSocial.class);
    }

    public UserSocial findByUserId(String userId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("userId", new Condition().withAttributeValueList(new AttributeValue(userId)).withComparisonOperator(ComparisonOperator.EQ));
        PaginatedScanList<UserSocial> result = pm.scan(UserSocial.class, scan);
        return !result.isEmpty() ? result.get(0) : null;
    }

    public UserSocial findByUserId(String provider, String userId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("provider", new Condition().withAttributeValueList(new AttributeValue(provider)).withComparisonOperator(ComparisonOperator.EQ));
        scan.addFilterCondition("userId", new Condition().withAttributeValueList(new AttributeValue(userId)).withComparisonOperator(ComparisonOperator.EQ));
        PaginatedScanList<UserSocial> result = pm.scan(UserSocial.class, scan);
        return !result.isEmpty() ? result.get(0) : null;
    }


    public UserSocial findBySocialId(String socialId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("socialId", new Condition().withAttributeValueList(new AttributeValue(socialId)).withComparisonOperator(ComparisonOperator.EQ));
        PaginatedScanList<UserSocial> result = pm.scan(UserSocial.class, scan);
        return !result.isEmpty() ? result.get(0) : null;
    }

    public UserSocial findBySocialId(String provider, String socialId) {
        UserSocial hashKeyValues = new UserSocial();
        hashKeyValues.setProvider(provider);
        List<UserSocial> result = pm.query(UserSocial.class, new DynamoDBQueryExpression<UserSocial>().withHashKeyValues(hashKeyValues).withRangeKeyCondition("socialId", new Condition().withAttributeValueList(new AttributeValue().withS(socialId)).withComparisonOperator(ComparisonOperator.EQ.toString())));
        return !result.isEmpty() ? result.get(0) : null;
    }

    /**
     * Add a UserSocial with the userId of oldUser. This method is used when a user is logged in with one social account, e.g. Facebook, and wants to add (link) another social account, for example Twitter.
     */
    public boolean addLinkedAccount(final AuthUser oldAuthUser, final AuthUser newAuthUser) {
        final User oldUser = new UserDAO().findByAuthUserIdentity(oldAuthUser);
        UserSocial newUserSocial = new UserSocial();
        newUserSocial.setProvider(newAuthUser.getProvider());
        newUserSocial.setSocialId(newAuthUser.getId());
        newUserSocial.setUserId(oldUser.getId());
        return save(newUserSocial);
    }

    public void saveFacebookAccessToken(final String facebookId,
                                        final String accessToken) {
        try {
//		UserSocial hashKey = new UserSocial();
//		hashKey.setProvider("facebook");
//		UserSocial userSocial = get(hashKey, facebookId);
            UserSocial userSocial = findBySocialId("facebook", facebookId);
            if (userSocial != null) {
                userSocial.setAccessToken(accessToken);
                save(userSocial);
            } else {
                throw new Exception("Fuck it, something went wrong. Wanted to saveOld a Facebook access token in our database. But: UserSocial object is null.");
            }
        } catch (Exception e) {
            Logger.error(e.getLocalizedMessage());
        }


    }
}
