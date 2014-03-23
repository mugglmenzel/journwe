package controllers.api.json;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.UserManager;
import models.adventure.Adventure;
import models.auth.SecuredUser;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.adventure.PlaceOptionDAO;
import models.dao.adventure.TimeOptionDAO;
import models.dao.user.UserDAO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.Routes;
import play.api.Play;
import play.cache.Cache;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static play.data.Form.form;


public class ApplicationController extends Controller {


    @Security.Authenticated(SecuredUser.class)
    public static Result getMyAdventures() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        final String userId = usr != null ? UserManager.findByAuthUserIdentity(usr).getId() : null;
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


            return ok(count == 10 && (lastId == null || "".equals(lastId)) ?
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
//                                        node.put("link", admin.core.routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
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



    /**
     * Returns a list of all admin.core.routes to handle in the javascript
     */
    public static Result routes() {
        response().setContentType("text/javascript");

        /*
        Set<play.core.Router.JavascriptReverseRoute> admin.core.routes = new HashSet<play.core.Router.JavascriptReverseRoute>();


        Reflections reflections = new Reflections("controllers.api.json.javascript");
        for(Class<? extends Object> clazz : reflections.getSubTypesOf(Object.class))
            for(Method met : clazz.getMethods())
                     admin.core.routes.add(new Router.JavascriptReverseRoute("controllers.api.json", met.getName()));
        */

        return ok("define(function(){" + // Make it AMD compatible
                Routes.javascriptRouter("routes",
                        controllers.api.json.routes.javascript.ApplicationController.getMyAdventures(),
                        controllers.api.json.routes.javascript.ApplicationController.getPublicAdventures(),
                        controllers.api.json.routes.javascript.CategoryController.getCategories(),
                        controllers.api.json.routes.javascript.CategoryController.categoriesOptionsMap(),
                        controllers.api.json.routes.javascript.CategoryController.getInspirations(),
                        controllers.api.json.routes.javascript.InspirationController.getUrls(),
                        controllers.api.json.routes.javascript.InspirationController.getTips(),
                        controllers.api.json.routes.javascript.InspirationController.getImages(),
                        controllers.api.json.routes.javascript.InspirationController.addTip(),
                        controllers.api.json.routes.javascript.UserController.getNotifications(),
                        controllers.api.json.routes.javascript.UserController.setMailDigestFrequency(),
                        controllers.api.json.routes.javascript.UserController.getAdventures(),
                        controllers.api.json.routes.javascript.AdventureController.updateImage(),
                        controllers.api.json.routes.javascript.AdventureController.getPhotos(),
                        controllers.api.json.routes.javascript.AdventureController.addPhoto(),
                        controllers.api.json.routes.javascript.AdventureController.updatePlaceVoteDeadline(),
                        controllers.api.json.routes.javascript.AdventureController.updateTimeVoteDeadline(),
                        controllers.api.json.routes.javascript.AdventureController.updatePlaceVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureController.updateTimeVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureController.placeVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureController.updateCategory(),
                        controllers.api.json.routes.javascript.AdventureController.updatePublic(),
                        controllers.api.json.routes.javascript.AdventureEmailController.listEmails(),
                        controllers.api.json.routes.javascript.AdventureEmailController.getEmail(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.getPlaces(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.getFavoritePlace(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.setFavoritePlace(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.addPlace(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.vote(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.deletePlace(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.getAdventurers(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.getParticipants(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.getOtherParticipants(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.getInvitees(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.getApplicants(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.participateStatus(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.invite(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.adopt(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.deny(),
                        controllers.api.json.routes.javascript.AdventurePeopleController.autocomplete(),
                        controllers.api.json.routes.javascript.AdventureTimeController.getTimes(),
                        controllers.api.json.routes.javascript.AdventureTimeController.getFavoriteTime(),
                        controllers.api.json.routes.javascript.AdventureTimeController.setFavoriteTime(),
                        controllers.api.json.routes.javascript.AdventureTimeController.addTime(),
                        controllers.api.json.routes.javascript.AdventureTimeController.vote(),
                        controllers.api.json.routes.javascript.AdventureTimeController.deleteTime(),
                        controllers.api.json.routes.javascript.AdventureTodoController.getTodos(),
                        controllers.api.json.routes.javascript.AdventureTodoController.addTodo(),
                        controllers.api.json.routes.javascript.AdventureTodoController.setTodo(),
                        controllers.api.json.routes.javascript.AdventureTodoController.deleteTodo(),
                        controllers.api.json.routes.javascript.AdventureTodoController.getTodoAffiliateItems(),
                        controllers.api.json.routes.javascript.AdventureFileController.uploadFile(),
                        controllers.api.json.routes.javascript.AdventureFileController.listFiles(),
                        controllers.api.json.routes.javascript.AdventureFileController.deleteFile(),
                        controllers.api.json.routes.javascript.AdventureTimelineController.get(),
                        controllers.api.json.routes.javascript.AdventureTimelineController.getLogs(),
                        controllers.api.json.routes.javascript.AdventureTimelineController.getEmails(),
                        controllers.api.json.routes.javascript.AdventureTimelineController.getComments(),
                        controllers.api.json.routes.javascript.AdventureTimelineController.getFiles(),
                        controllers.api.json.routes.javascript.CommentController.saveComment(),
                        controllers.api.json.routes.javascript.CommentController.listComments(),
                        controllers.api.json.routes.javascript.UserController.getEmails()
                ) + ";; return routes;});");
    }


    public static Result messages() {
        response().setContentType("text/javascript");
        return ok("define(function(){ var messages = " + Json.toJson(scala.collection.JavaConversions.asJavaMap(play.api.i18n.Messages.messages(Play.current()).get(Http.Context.current().lang().code()).get())).toString() + "; return messages;});");
    }


    public static void clearUserCache(final String userId) {
        Cache.remove("user." + userId + ".myadventures");
    }

}
