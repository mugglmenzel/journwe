package models.dao.manytomany;

import models.category.Category;
import models.dao.category.CategoryDAO;
import models.inspiration.Inspiration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by markus on 18/01/14.
 */
public class CategoryToInspirationDAO extends ManyToManyDAO<Category, Inspiration> {

    public CategoryToInspirationDAO() {
        super(Category.class, Inspiration.class);
    }

    public Map<String, String> getOptions(String insId) {
        Map<String, String> result = new HashMap<String, String>();
        for (Category cat : listM(insId, "", 0)) {
            result.put(cat.getId(), cat.getName());
        }
        return result;
    }

}
