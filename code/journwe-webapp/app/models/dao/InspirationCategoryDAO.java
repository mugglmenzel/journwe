package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.category.Category;
import models.dao.common.CommonRangeEntityDAO;
import models.inspiration.InspirationCategory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 10.09.13
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class InspirationCategoryDAO extends CommonRangeEntityDAO<InspirationCategory> {

    public InspirationCategoryDAO() {
        super(InspirationCategory.class);
    }


    public List<InspirationCategory> all(String catId, String lastKey, int limit) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        if (limit > 0) query.setLimit(limit);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("categoryId", new AttributeValue(catId));
            startkey.put("inspirationId", new AttributeValue(lastKey));
            query.setExclusiveStartKey(startkey);
        }

        InspirationCategory insCat = new InspirationCategory();
        insCat.setCategoryId(catId);
        query.setHashKeyValues(insCat);

        return pm.query(InspirationCategory.class, query);
    }

    public List<InspirationCategory> getCategories(String insId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("inspirationId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(insId)));
        return pm.scan(InspirationCategory.class, scan);
    }

    public Map<String, String> getOptions(String insId) {
        Map<String, String> result = new HashMap<String, String>();
        for (InspirationCategory insCat : getCategories(insId)) {
            Category cat = new CategoryDAO().get(insCat.getCategoryId());
            result.put(cat.getId(), cat.getName());
        }
        return result;
    }

}
