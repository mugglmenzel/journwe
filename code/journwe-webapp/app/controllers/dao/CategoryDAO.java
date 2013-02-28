package controllers.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.InspirationCategory;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;

import controllers.dao.common.CommonParentDAO;

public class CategoryDAO extends CommonParentDAO<InspirationCategory> {

	@Override
	public InspirationCategory get(String id) {
		return pm.load(InspirationCategory.class, id, new DynamoDBMapperConfig(
				ConsistentReads.EVENTUAL));
	}

	public List<InspirationCategory> all(int max) {
		return pm.scanPage(InspirationCategory.class,
				new DynamoDBScanExpression().withLimit(max)).getResults();
	}
	
	public Map<String, String> allOptionsMap(int max) {
		Map<String, String> result = new HashMap<String, String>();
		for (InspirationCategory in : all(max))
			result.put(in.getId(), in.getName());
		return result;
	}
	
}
