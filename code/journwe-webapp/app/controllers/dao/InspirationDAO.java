package controllers.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.dao.common.CommonEntityDAO;
import models.Inspiration;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;

import controllers.dao.common.CommonRangeEntityDAO;

public class InspirationDAO extends CommonEntityDAO<Inspiration> {

	/**
	 * 
	 */
	public InspirationDAO() {
		super(Inspiration.class);
	}

	public List<Inspiration> all(int max) {
		return pm.scanPage(Inspiration.class,
				new DynamoDBScanExpression().withLimit(max)).getResults();
	}
	
	public List<Inspiration> all(int max, String catId) {
		return pm.queryPage(Inspiration.class,
				new DynamoDBQueryExpression(new AttributeValue(catId)).withLimit(max)).getResults();
	}

	public Map<String, String> allOptionsMap(int max) {
		Map<String, String> result = new HashMap<String, String>();
		for (Inspiration in : all(max))
			result.put(in.getId(), in.getName());
		return result;
	}
}
