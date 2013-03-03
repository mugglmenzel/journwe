package controllers.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.ResourceNotFoundException;

import controllers.dao.common.CommonParentDAO;

public class UserDAO extends CommonParentDAO<User> {

	@Override
	public User get(String id) {
		try {
			return pm.load(User.class, id, new DynamoDBMapperConfig(
					ConsistentReads.EVENTUAL));
		} catch(ResourceNotFoundException e){
			return null;
		}
	}
	
}
