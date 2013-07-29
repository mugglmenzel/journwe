package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.CommonEntityDAO;
import models.Category;
import models.Inspiration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDAO extends CommonEntityDAO<Category> {

    public CategoryDAO() {
        super(Category.class);
    }

    public List<Category> all() {
        return pm.scan(Category.class,
                new DynamoDBScanExpression());
    }

    public Map<String, String> allOptionsMap() {
        Map<String, String> result = new HashMap<String, String>();
        for (Category in : all())
            result.put(in.getId(), in.getName());
        return result;
    }

    public Integer countInspirations(String id) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        Inspiration ins = new Inspiration();
        ins.setCategoryId(id);
        query.setHashKeyValues(ins);
        return pm.count(Inspiration.class, query);
    }
}
