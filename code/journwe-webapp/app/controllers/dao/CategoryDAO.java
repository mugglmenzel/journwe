package controllers.dao;

import models.InspirationCategory;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;

import controllers.dao.common.CommonParentDAO;

public class CategoryDAO extends CommonParentDAO<InspirationCategory> {

	@Override
	public InspirationCategory get(String id) {
		return pm.load(InspirationCategory.class, id, new DynamoDBMapperConfig(
				ConsistentReads.EVENTUAL));
	}

}
