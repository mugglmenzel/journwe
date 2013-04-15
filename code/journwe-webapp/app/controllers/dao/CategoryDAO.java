package controllers.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.dao.common.CommonEntityDAO;
import models.Category;
import models.Inspiration;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;

public class CategoryDAO extends CommonEntityDAO<Category> {

    public CategoryDAO() {
        super(Category.class);
    }

    @Override
	public Category get(String id) {
		return pm.load(Category.class, id, new DynamoDBMapperConfig(
				ConsistentReads.EVENTUAL));
	}

	public List<Category> all(int max) {
		return pm.scanPage(Category.class,
				new DynamoDBScanExpression().withLimit(max)).getResults();
	}

	public Map<String, String> allOptionsMap(int max) {
		Map<String, String> result = new HashMap<String, String>();
		for (Category in : all(max))
			result.put(in.getId(), in.getName());
		return result;
	}

	public Integer countInspirations(String id) {
		return pm.count(Inspiration.class, new DynamoDBQueryExpression(
				new AttributeValue(id)));
	}
}
