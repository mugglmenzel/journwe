package controllers.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Inspiration;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;

import controllers.dao.common.CommonChildDAO;

public class InspirationDAO extends CommonChildDAO<Inspiration> {

	/**
	 * 
	 */
	public InspirationDAO() {
		super();
	}

	@Override
	public Inspiration get(String parentId, String id) {
	
		return pm.load(Inspiration.class, parentId, id,
				new DynamoDBMapperConfig(ConsistentReads.EVENTUAL));
	}

	public List<Inspiration> all(int max) {
		return pm.scanPage(Inspiration.class,
				new DynamoDBScanExpression().withLimit(max)).getResults();
	}

	public Map<String, String> allOptionsMap(int max) {
		Map<String, String> result = new HashMap<String, String>();
		for (Inspiration in : all(max))
			result.put(in.getId(), in.getName());
		return result;
	}
}
