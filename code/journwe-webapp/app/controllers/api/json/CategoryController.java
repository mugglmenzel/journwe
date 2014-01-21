package controllers.api.json;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.typesafe.config.ConfigFactory;
import models.auth.SecuredAdminUser;
import models.category.Category;
import models.category.CategoryHierarchy;
import models.dao.category.CategoryDAO;
import models.dao.category.CategoryHierarchyDAO;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.category.manage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static play.data.Form.form;

public class CategoryController extends Controller {

    private static final String S3_BUCKET_CATEGORY_IMAGES = "journwe-category-images";

    private static Form<Category> catForm = form(Category.class);

    public static Result categoriesOptionsMap() {
        try {
            return ok(Cache.getOrElse("categories.optionsMap", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    Map<String, String> optionsMap = new CategoryDAO().allOptionsMap();
                    for (String catId : optionsMap.keySet()) {
                        if (catId != null) {
                            ObjectNode node = Json.newObject();
                            node.put("id", catId);
                            node.put("name", optionsMap.get(catId));
                            results.add(node);
                        }
                    }

                    return Json.toJson(results).toString();
                }
            }, 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate categories optionsMap", e);
            return internalServerError();
        }
    }



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

        clearCache(superCatId);

        return ok(Json.toJson(hier));
    }


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result updateCountCache() {
        new CategoryDAO().updateCategoryCountCache();
        return ok();
    }


    private static void clearCache() {
        clearCache(Category.SUPER_CATEGORY);
    }

    private static void clearCache(String superCatId) {
        new CategoryDAO().updateCategoryCountCache();
        Cache.remove("subCategoriesOf." + superCatId);
        Cache.remove("categories.optionsMap");
    }

}
