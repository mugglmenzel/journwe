package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.category.CategoryHierarchy;
import models.dao.common.CommonEntityDAO;
import models.category.Category;
import models.Inspiration;
import play.Logger;

import java.util.ArrayList;
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

    public List<Category> allOfCategory(String categoryId) {
        Logger.debug("retrieving hierarchy for " + categoryId);

        DynamoDBQueryExpression query = new DynamoDBQueryExpression();

        CategoryHierarchy hier = new CategoryHierarchy();
        hier.setSuperCategoryId(categoryId);
        query.setHashKeyValues(hier);

        List<CategoryHierarchy> hiers = pm.query(CategoryHierarchy.class, query);

        List<Category> results = new ArrayList<Category>();
        for(CategoryHierarchy h : hiers)
            results.add(get(h.getSubCategoryId()));

        return results;
    }

    public Integer countInspirations(String id) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        Inspiration ins = new Inspiration();
        ins.setCategoryId(id);
        query.setHashKeyValues(ins);
        return pm.count(Inspiration.class, query);
    }
}
