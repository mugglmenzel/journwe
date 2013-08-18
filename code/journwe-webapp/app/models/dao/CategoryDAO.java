package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.Inspiration;
import models.category.Category;
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
        long start = new Date().getTime();
        Logger.debug("Starting count for " + id + " at " + start);
        Integer sum = countInspirations(id);
        ExecutorService exec = Executors.newCachedThreadPool();
        List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
        for (final Category cat : allSubcategory(id))
            if (cat != null) callables.add(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return countInspirationsHierarchy(cat.getId());  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        try {
            List<Future<Integer>> futures = exec.invokeAll(callables);
            for (Future<Integer> f : futures)
                sum += f.get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        long end = new Date().getTime();
        Logger.debug("Ending count for " + id + " at " + end + ", took " + (end - start));
        return sum;
    }
}
