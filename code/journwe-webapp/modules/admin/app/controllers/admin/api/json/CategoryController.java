package controllers.admin.api.json;

import models.GlobalParameters;
import models.auth.SecuredAdminUser;
import models.category.Category;
import models.category.CategoryHierarchy;
import models.dao.category.CategoryDAO;
import models.dao.category.CategoryHierarchyDAO;
import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import static play.data.Form.form;

public class CategoryController extends Controller {

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result setSuperCategory() {
        DynamicForm df = form().bindFromRequest();
        String catId = df.get("category");
        String superCatId = df.get("superCategory");
        Logger.debug("adding " + catId + " to " + superCatId);

        for (CategoryHierarchy catHier : new CategoryHierarchyDAO().categoryAsSub(catId))
            new CategoryHierarchyDAO().delete(catHier);
        CategoryHierarchy hier = new CategoryHierarchy();
        hier.setSuperCategoryId(superCatId);
        hier.setSubCategoryId(catId);
        new CategoryHierarchyDAO().save(hier);

        clearCacheOfCat(superCatId);

        return ok(Json.toJson(hier));
    }


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result updateCountCache() {
        new CategoryDAO().updateCategoryCountCache();
        clearCache();
        return ok();
    }


    public static void clearCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cache.remove("categories.optionsMap");
                clearCacheOfCat(Category.SUPER_CATEGORY);
                for (Category cat : new CategoryDAO().all())
                    clearCacheOfCat(cat.getId());
            }
        }).start();
    }

    private static void clearCacheOfCat(String superCatId) {
        Cache.remove("subCategoriesOf." + superCatId);
        Cache.remove("category." + superCatId + ".inspirations." + "" + "." + GlobalParameters.DEFAULT_INSPIRATION_COUNT);
    }


}
