package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.Inspiration;
import models.category.Category;
import models.category.CategoryCount;
import models.category.CategoryHierarchy;
import models.dao.common.CommonEntityDAO;
import play.Logger;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
            if (in != null) result.put(in.getId(), in.getName());
        return result;
    }

    public List<Category> allSubcategory(String categoryId) {
        List<CategoryHierarchy> hiers = new CategoryHierarchyDAO().categoryAsSuper(categoryId);

        List<Category> results = new ArrayList<Category>();
        for (CategoryHierarchy h : hiers)
            if (h != null) results.add(get(h.getSubCategoryId()));

        return results;
    }

    public Integer countSubcategory(String categoryId) {
        return new CategoryHierarchyDAO().countCategoryAsSuper(categoryId);
    }

    public Integer countInspirations(String id) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        Inspiration ins = new Inspiration();
        ins.setCategoryId(id);
        query.setHashKeyValues(ins);
        return pm.count(Inspiration.class, query);
    }



    public Integer countInspirationsHierarchy(String id) {
        Integer sum = countInspirations(id);
        for (Category cat : allSubcategory(id))
            if (cat != null) sum += countInspirationsHierarchy(cat.getId());
        return sum;
    }

    public Integer countInspirationsHierarchyCached(String id) {
        CategoryCount cc = new CategoryCountDAO().get(id);
        return cc != null ? cc.getCount() : countInspirationsHierarchy(id);
    }

}
