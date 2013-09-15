package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.AdventureCategory;
import models.category.Category;
import models.dao.common.CommonRangeEntityDAO;
import models.inspiration.InspirationCategory;

import java.util.ArrayList;
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
public class AdventureCategoryDAO extends CommonRangeEntityDAO<AdventureCategory> {

    public AdventureCategoryDAO() {
        super(AdventureCategory.class);
    }


    public List<AdventureCategory> all(String catId, String lastKey, int limit) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        if (limit > 0) query.setLimit(limit);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("categoryId", new AttributeValue(catId));
            startkey.put("adventureId", new AttributeValue(lastKey));
            query.setExclusiveStartKey(startkey);
        }

        AdventureCategory advCat = new AdventureCategory();
        advCat.setCategoryId(catId);
        query.setHashKeyValues(advCat);

        query.setScanIndexForward(false);

        return pm.query(AdventureCategory.class, query);
    }

    public AdventureCategory getCategory(String advId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("adventureId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(advId)));
        List<AdventureCategory> result = pm.scan(AdventureCategory.class, scan);

        return result != null && result.size() > 0 ? result.get(0) : null;
    }


}
