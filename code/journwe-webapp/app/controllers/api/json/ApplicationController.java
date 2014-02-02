package controllers.api.json;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.adventure.Adventure;
import models.auth.SecuredUser;
import models.category.Category;
import models.category.CategoryCount;
import models.category.CategoryHierarchy;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.adventure.PlaceOptionDAO;
import models.dao.adventure.TimeOptionDAO;
import models.dao.category.CategoryCountDAO;
import models.dao.category.CategoryDAO;
import models.dao.category.CategoryHierarchyDAO;
import models.dao.manytomany.CategoryToInspirationDAO;
import models.dao.user.UserDAO;
import models.inspiration.Inspiration;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static play.data.Form.form;


public class ApplicationController extends Controller {


    @Security.Authenticated(SecuredUser.class)
    public static Result getMyAdventures() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        final String userId = usr != null ? new UserDAO().findByAuthUserIdentity(usr).getId() : null;
        if (userId == null) return badRequest();

        DynamicForm data = form().bindFromRequest();
        final String lastId = data.get("lastId");
        final int count = data.get("count") != null ? new Integer(data.get("count")).intValue() : 10;

        try {
            Callable<String> resultsCallable = new Callable<String>() {


                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    for (Adventure adv : new AdventurerDAO().listAdventuresByUser(userId, lastId, count)) {
                        if (adv != null) {
                            ObjectNode node = Json.newObject();
                            node.put("id", adv.getId());
                            node.put("link", controllers.html.routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
                            node.put("image", adv.getImage());
                            node.put("imageTimestamp", adv.getImageTimestamp());
                            node.put("name", adv.getName());
                            node.put("peopleCount", new AdventurerDAO().userCountByAdventure(adv.getId()));
                            node.put("favoritePlace", adv.getFavoritePlaceId() != null ? Json.toJson(new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId())) : null);
                            node.put("favoriteTime", adv.getFavoriteTimeId() != null ? Json.toJson(new TimeOptionDAO().get(adv.getId(), adv.getFavoriteTimeId())) : null);
                            node.put("status", new AdventurerDAO().get(adv.getId(), userId).getParticipationStatus().name());
                            results.add(node);
                        }
                    }
                    return Json.toJson(results).toString();
                }
            };


            return ok(count == 10 && lastId == null ?
                    Cache.getOrElse("user." + userId + ".myadventures", resultsCallable, 24 * 3600)
                    : resultsCallable.call()).as("application/json");

        } catch (Exception e) {
            Logger.error("Couldn't generate my adventures for user " + userId, e);
            return internalServerError();
        }
    }


    public static Result getPublicAdventures() {
        DynamicForm data = form().bindFromRequest();
        String lastId = data.get("lastId");
        int count = new Integer(data.get("count")).intValue();
        String inspirationId = data.get("inspirationId");

        List<ObjectNode> result = new ArrayList<ObjectNode>();
        for (Adventure adv : new AdventureDAO().listPublicAdventuresByInspiration(inspirationId, lastId, count)) {
            if (adv != null) {
                ObjectNode node = Json.newObject();
                node.put("id", adv.getId());
                node.put("link", controllers.html.routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
                node.put("image", adv.getImage());
                node.put("name", adv.getName());
                node.put("peopleCount", new AdventurerDAO().userCountByAdventure(adv.getId()));
                node.put("favoritePlace", adv.getFavoritePlaceId() != null ? Json.toJson(new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId())) : null);
                node.put("favoriteTime", adv.getFavoriteTimeId() != null ? Json.toJson(new TimeOptionDAO().get(adv.getId(), adv.getFavoriteTimeId())) : null);
                node.put("lat", adv.getFavoritePlaceId() != null ? new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId()).getLatitude().floatValue() : 0F);
                node.put("lng", adv.getFavoritePlaceId() != null ? new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId()).getLongitude().floatValue() : 0F);

                result.add(node);
            }
        }

        return ok(Json.toJson(result));
    }

    public static Result getPublicAdventuresOfCategory(final String catId) {
        DynamicForm data = form().bindFromRequest();
        final String lastId = data.get("lastId");
        int countParam = 10;
        try {
            countParam = data.get("count") != null ? new Integer(data.get("count")).intValue() : countParam;
        } catch (Exception e) {
            return badRequest("Count is not a number.");
        }
        final int count = countParam;

        try {
            return ok(Cache.getOrElse("category." + catId + ".publicadventures." + lastId + "." + count, new Callable<String>() {


                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>(count);
                    String lastAdventureId = lastId;

                    boolean more = true;
                    if (count > 0)
                        while (more) {
                            // TODO
//                            List<AdventureCategory> advCats = new AdventureToCategoryDAO().all(catId, lastAdventureId, userCountByAdventure);
//                            more = advCats.size() > 0;
//                            for (AdventureCategory advCat : advCats) {
//                                if (more) {
//                                    Adventure adv = new AdventureDAO().get(advCat.getAdventureId());
//                                    if (adv != null && adv.isPublish()) {
//                                        ObjectNode node = Json.newObject();
//                                        node.put("id", adv.getId());
//                                        node.put("link", routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
//                                        node.put("image", adv.getImage());
//                                        node.put("name", adv.getName());
//                                        node.put("peopleCount", new AdventurerDAO().userCountByAdventure(adv.getId()));
//                                        node.put("favoritePlace", adv.getFavoritePlaceId() != null ? Json.toJson(new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId())) : null);
//                                        node.put("favoriteTime", adv.getFavoriteTimeId() != null ? Json.toJson(new TimeOptionDAO().get(adv.getId(), adv.getFavoriteTimeId())) : null);
//                                        node.put("lat", adv.getFavoritePlaceId() != null ? new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId()).getLatitude().floatValue() : 0F);
//                                        node.put("lng", adv.getFavoritePlaceId() != null ? new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId()).getLongitude().floatValue() : 0F);
//
//                                        results.add(node);
//
//                                        more = results.size() < userCountByAdventure;
//                                    }
//                                    lastAdventureId = advCat.getAdventureId();
//                                }
//                            }

                        }
                    return Json.toJson(results).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate public adventures for category " + catId, e);
            return internalServerError();
        }
    }


    public static Result getInspirations(final String catId) {
        DynamicForm data = form().bindFromRequest();
        final String lastId = data.get("lastId");
        int countParam = 10;
        try {
            countParam = data.get("count") != null ? new Integer(data.get("count")).intValue() : 10;
        } catch (Exception e) {
            return badRequest("Count is not a number.");
        }
        final int count = countParam;

        try {
            return ok(Cache.getOrElse("category." + catId + ".inspirations." + lastId + "." + count, new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final Date now = new Date();

                    List<ObjectNode> results = new ArrayList<ObjectNode>();

                    String lastInspirationId = lastId;

                    boolean more = true;
                    if (count > 0)
                        while (more) {
                            List<Inspiration> inspirations = new CategoryToInspirationDAO().listN(catId, lastInspirationId, count);
                            more = inspirations.size() > 0;
                            for (Inspiration ins : inspirations) {
                                if (more) {
                                    if (ins != null && (ins.getTimeEnd() == null || ins.getTimeEnd().after(now))) {
                                        ObjectNode node = Json.newObject();
                                        node.put("id", ins.getId());
                                        node.put("link", controllers.html.routes.InspirationController.get(ins.getId()).absoluteURL(request()));
                                        node.put("image", ins.getImage());
                                        node.put("name", ins.getName());
                                        node.put("lat", ins.getPlaceLatitude() != null ? ins.getPlaceLatitude().floatValue() : 0F);
                                        node.put("lng", ins.getPlaceLongitude() != null ? ins.getPlaceLongitude().floatValue() : 0F);

                                        results.add(node);
                                        more = results.size() < count;
                                    }
                                    lastInspirationId = ins.getId();
                                }
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


    public static void clearUserCache(final String userId) {
        Cache.remove("user." + userId + ".myadventures");
    }

}
