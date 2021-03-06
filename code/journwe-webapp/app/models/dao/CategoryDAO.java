package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.category.Category;
import models.category.CategoryCount;
import models.category.CategoryHierarchy;
import models.dao.common.CommonEntityDAO;
import models.inspiration.InspirationCategory;
import play.cache.Cache;

import java.util.*;

public class CategoryDAO extends CommonEntityDAO<Category> {

    public CategoryDAO() {
        super(Category.class);
    }

    public Category get(String id) {
        if (Category.SUPER_CATEGORY.equals(id)) {
            Category result = new Category();
            result.setId(Category.SUPER_CATEGORY);
            result.setName(Category.SUPER_CATEGORY);
            return result;
        }
        return super.get(id);
    }

    public List<Category> all() {
        List<Category> scanResults = pm.scan(Category.class,
                new DynamoDBScanExpression());
        List<Category> results = new ArrayList<Category>();
        for (Category cat : scanResults) {
            results.add(cat);
        }

        Collections.sort(results, new Comparator<Category>() {
            @Override
            public int compare(Category category, Category category2) {
                return category.getName().compareTo(category2.getName());  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        return results;
    }

    public Map<String, String> allOptionsMap() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (Category in : all())
            if (in != null) result.put(in.getId(), in.getName());
        return result;
    }

    public List<Category> allSubcategory(String categoryId) {
        List<CategoryHierarchy> hiers = new CategoryHierarchyDAO().categoryAsSuper(categoryId);

        List<Category> results = new ArrayList<Category>();
        for (CategoryHierarchy h : hiers)
            if (h != null) results.add(get(h.getSubCategoryId()));

        Collections.sort(results, new Comparator<Category>() {
            @Override
            public int compare(Category category, Category category2) {
                return category.getName().compareTo(category2.getName());  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        return results;
    }

    public Integer countSubcategory(String categoryId) {
        return new CategoryHierarchyDAO().countCategoryAsSuper(categoryId);
    }

    public Integer countInspirations(String id) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        InspirationCategory ins = new InspirationCategory();
        ins.setCategoryId(id);
        query.setHashKeyValues(ins);
        return pm.count(InspirationCategory.class, query);
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


    public Category getSuperCategory(String id) {
        List<CategoryHierarchy> superCats = new CategoryHierarchyDAO().categoryAsSub(id);

        return superCats.size() > 0 ? get(superCats.get(0).getSuperCategoryId()) : null;
    }

    public void updateCategoryCountCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Category cat : all()) {
                    CategoryCount cc = new CategoryCount();
                    cc.setCategoryId(cat.getId());
                    cc.setCount(new CategoryDAO().countInspirationsHierarchy(cat.getId()));
                    new CategoryCountDAO().save(cc);
                }
            }
        }).start();

    }

    public void clearCache() {
        clearCache(Category.SUPER_CATEGORY);
    }

    public void clearCache(String superCatId) {
        Cache.remove("subCategoriesOf." + superCatId);
        Cache.remove("categories.optionsMap");
        updateCategoryCountCache();
    }

}
