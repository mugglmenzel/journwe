package models.dao.category;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.category.Category;
import models.category.CategoryCount;
import models.category.CategoryHierarchy;
import models.dao.common.CommonEntityDAO;
import models.dao.manytomany.ManyToManyCountQuery;
import models.dao.queries.RangeCountQuery;
import models.inspiration.Inspiration;

import java.util.*;

public class CategoryDAO extends CommonEntityDAO<Category> {

    private ManyToManyCountQuery<Category,Inspiration> categoryToInspirationCountQuery;

    public CategoryDAO() {
        super(Category.class);
        categoryToInspirationCountQuery = new ManyToManyCountQuery<Category,Inspiration>(Category.class,Inspiration.class);
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
        // Scan should be fine since number of categories will not grow to large numbers.
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

    public Integer countInspirationsByCategory(String categoryId) {
        return categoryToInspirationCountQuery.countN(categoryId);
    }

    public Integer countInspirationsHierarchy(String categoryId) {
        Integer sum = countInspirationsByCategory(categoryId);
        for (Category cat : allSubcategory(categoryId))
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


}
