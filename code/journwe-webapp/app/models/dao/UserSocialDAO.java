package models.dao;

import java.util.HashMap;
import java.util.Map;

import play.Logger;

import models.user.UserSocial;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
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

    public UserSocial findBySocialId(String provider, String socialId) {
    	DynamoDBQueryExpression<UserSocial> queryExpression = new DynamoDBQueryExpression<UserSocial>();        
    	UserSocial hashKeyValues = new UserSocial();
        hashKeyValues.setProvider(provider);
		queryExpression.setHashKeyValues(hashKeyValues);
		Map<String, Condition> rangeKeyConditions = new HashMap<String, Condition>();
		Logger.debug("Range key kondition: socialId == " + socialId);
		rangeKeyConditions.put("socialId", new Condition().withAttributeValueList(new AttributeValue().withS(socialId)).withComparisonOperator(ComparisonOperator.EQ.toString()));
		queryExpression.setRangeKeyConditions(rangeKeyConditions);
		PaginatedQueryList<UserSocial> result = pm.query(UserSocial.class, queryExpression);
        Logger.debug("UserSocialDAO.findBySocialId("+provider+", "+socialId+"). Found the following user(s):");
        for(UserSocial us: result)
        	Logger.debug("User with user id: "+us.getUserId());
        return !result.isEmpty() ? result.get(0) : null;
    }
    
	public void saveFacebookAccessToken(final String facebookId,
			final String accessToken) {
		try {
//		UserSocial hashKey = new UserSocial();
//		hashKey.setProvider("facebook");
//		UserSocial userSocial = get(hashKey, facebookId);
		UserSocial userSocial = findBySocialId("facebook", facebookId);
		if(userSocial!=null) {
			userSocial.setAccessToken(accessToken);
			save(userSocial);
		} else {
			throw new Exception("Fuck it, something went wrong. Wanted to save a Facebook access token in our database. But: UserSocial object is null.");
		} }
		catch(Exception e) {
			Logger.error(e.getLocalizedMessage());
		}
		
		
	}
}
