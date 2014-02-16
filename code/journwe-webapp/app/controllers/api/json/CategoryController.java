package controllers.api.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.auth.SecuredAdminUser;
import models.category.Category;
import models.category.CategoryCount;
import models.category.CategoryHierarchy;
import models.dao.category.CategoryCountDAO;
import models.dao.category.CategoryDAO;
import models.dao.category.CategoryHierarchyDAO;
import models.dao.manytomany.CategoryToInspirationDAO;
import models.inspiration.Inspiration;
import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static play.data.Form.form;

public class CategoryController extends Controller {

    private final static int DEFAULT_INSPIRATION_COUNT = 8;


    public static Result getCategories(final String superCatId) {

        try {
            return ok(Cache.getOrElse("subCategoriesOf." + superCatId, new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    for (CategoryHierarchy cat : new CategoryHierarchyDAO().categoryAsSuper(superCatId)) {
                        CategoryCount cc = new CategoryCountDAO().get(cat.getSubCategoryId());
                        if (cat != null && cc.getCount() > 0) {
                            Category c = new CategoryDAO().get(cc.getCategoryId());
                            if (c != null) {
                                ObjectNode node = Json.newObject();
                                node.put("id", c.getId());
                                node.put("name", c.getName());
                                node.put("link", controllers.html.routes.ApplicationController.categoryIndex(c.getId()).absoluteURL(request()));
                                node.put("image", c.getImage());
                                node.put("count", cc.getCount());
                                results.add(node);
                            }
                        }
                    }

                    return Json.toJson(results).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate sub-categories of " + superCatId, e);
            return internalServerError();
        }


    }

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

    public static Result getInspirations(final String catId) {
        DynamicForm data = form().bindFromRequest();
        final String lastId = data.get("lastId");
        int countParam = 8;
        try {
            countParam = data.get("count") != null ? new Integer(data.get("count")).intValue() : DEFAULT_INSPIRATION_COUNT;
        } catch (Exception e) {
            return badRequest("Count is not a number.");
        }
        final int count = countParam;

        try {
            return ok(Cache.getOrElse("category." + catId + ".inspirations." + lastId + "." + count, new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final Date now = new Date();
                    boolean more = true;

                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    if (count > 0) {
                        while (more) {
                            List<Inspiration> inspirations = new CategoryToInspirationDAO().listN(catId, lastId, count);
                            Logger.debug("inspirations found:" + inspirations);

                            for (Inspiration ins : inspirations)
                                if (ins != null && (ins.getTimeEnd() == null || ins.getTimeEnd().after(now))) {
                                    ObjectNode node = Json.newObject();
                                    node.put("id", ins.getId());
                                    node.put("link", controllers.html.routes.InspirationController.get(ins.getId()).absoluteURL(request()));
                                    node.put("image", ins.getImage());
                                    node.put("name", ins.getName());
                                    node.put("lat", ins.getPlaceLatitude() != null ? ins.getPlaceLatitude().floatValue() : 0F);
                                    node.put("lng", ins.getPlaceLongitude() != null ? ins.getPlaceLongitude().floatValue() : 0F);

                                    results.add(node);
                                }
                            more = results.size() < count && inspirations.size() >= count;
                        }

                    }
                    return Json.toJson(results).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate inspirations for category " + catId, e);
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

        clearCacheOfCat(superCatId);

        return ok(Json.toJson(hier));
    }


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result updateCountCache() {
        new CategoryDAO().updateCategoryCountCache();
        clearCache();
        return ok();
    }


    private static void clearCache() {
        new Thread(new Runnable(){
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
        Cache.remove("category." + superCatId + ".inspirations." + "" + "." + DEFAULT_INSPIRATION_COUNT);
    }



}
