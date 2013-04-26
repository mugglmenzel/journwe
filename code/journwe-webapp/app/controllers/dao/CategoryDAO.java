package controllers.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import controllers.dao.common.CommonEntityDAO;
import models.Category;
import models.Inspiration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDAO extends CommonEntityDAO<Category> {

    public CategoryDAO() {
        super(Category.class);
    }

    public List<Category> all(int max) {
        return pm.scan(Category.class,
                new DynamoDBScanExpression().withLimit(max));
    }

    public Map<String, String> allOptionsMap(int max) {
        Map<String, String> result = new HashMap<String, String>();
        for (Category in : all(max))
            result.put(in.getId(), in.getName());
        return result;
    }

    public Integer countInspirations(String id) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("inspirationCategoryId", new Condition().withAttributeValueList(new AttributeValue(id)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.count(Inspiration.class, scan);
    }
}
