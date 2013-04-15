package controllers.dao;

import controllers.dao.common.CommonEntityDAO;
import models.User;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodb.model.ResourceNotFoundException;

public class UserDAO extends CommonEntityDAO<User> {

    public UserDAO() {
        super(User.class);
    }
	
}
