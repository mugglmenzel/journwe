package controllers;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.adventure.Adventure;
import models.adventure.AdventureCategory;
import models.auth.SecuredAdminUser;
import models.auth.SecuredBetaUser;
import models.category.Category;
import models.category.CategoryCount;
import models.category.CategoryHierarchy;
import models.dao.*;
import models.inspiration.Inspiration;
import models.inspiration.InspirationCategory;
import models.user.Subscriber;
import org.codehaus.jackson.node.ObjectNode;
import play.cache.Cache;
import play.cache.Cached;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.about;
import views.html.admin;
import views.html.imprint;
import views.html.index.index;
import views.html.index.indexCat;
import views.html.index.indexVet;
import views.html.subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;


public class ApplicationController extends Controller {

    private static Form<Subscriber> subForm = form(Subscriber.class);

    public static Result index() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
                && (SecuredBetaUser.isBeta(usr) || SecuredBetaUser.isAdmin(usr))) {

            String userId = new UserDAO().findByAuthUserIdentity(usr).getId();
            if (new AdventurerDAO().isAdventurer(userId)) {
                return ok(indexVet.render());
            } else {

                return ok(index.render());
            }
        } else {
            return ok(subscribe.render(subForm));
        }
    }

    @Cached(key = "indexNew", duration = 3600)
    public static Result indexNew() {
        return ok(index.render());
    }

    public static Result categoryIndex(String catId) {
        if (Category.SUPER_CATEGORY.equals(catId)) return redirect(routes.ApplicationController.index());
        return ok(indexCat.render(new CategoryDAO().get(catId)));
    }

    public static Result getCategories(String superCatId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        for (CategoryHierarchy cat : new CategoryHierarchyDAO().categoryAsSuper(superCatId)) {
            CategoryCount cc = new CategoryCountDAO().get(cat.getSubCategoryId());
            if (cat != null && cc.getCount() > 0) {
                Category c = new CategoryDAO().get(cc.getCategoryId());
                if(c != null){
                ObjectNode node = Json.newObject();
                node.put("name", c.getName());
                node.put("link", routes.ApplicationController.categoryIndex(c.getId()).absoluteURL(request()));
                node.put("image", c.getImage());
                node.put("count", cc.getCount());
                results.add(node);
                }
            }
        }

        return ok(Json.toJson(results));
    }


    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getMyAdventures() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        String userId = new UserDAO().findByAuthUserIdentity(usr).getId();

        DynamicForm data = form().bindFromRequest();
        String lastId = data.get("lastId");
        int count = new Integer(data.get("count")).intValue();

        List<ObjectNode> result = new ArrayList<ObjectNode>();
        for (Adventure adv : new AdventureDAO().allOfUserId(userId, lastId, count)) {
            ObjectNode node = Json.newObject();
            node.put("id", adv.getId());
            node.put("link", routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
            node.put("image", adv.getImage());
            node.put("name", adv.getName());
            node.put("peopleCount", new AdventurerDAO().count(adv.getId()));
            node.put("favoritePlace", adv.getFavoritePlaceId() != null ? Json.toJson(new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId())) : null);
            node.put("favoriteTime", adv.getFavoriteTimeId() != null ? Json.toJson(new TimeOptionDAO().get(adv.getId(), adv.getFavoriteTimeId())) : null);

            result.add(node);
        }

        return ok(Json.toJson(result));
    }


    public static Result getPublicAdventures() {
        DynamicForm data = form().bindFromRequest();
        String lastId = data.get("lastId");
        int count = new Integer(data.get("count")).intValue();
        String inspirationId = data.get("inspirationId");

        List<ObjectNode> result = new ArrayList<ObjectNode>();
        for (Adventure adv : new AdventureDAO().allPublic(inspirationId, lastId, count)) {
            ObjectNode node = Json.newObject();
            node.put("id", adv.getId());
            node.put("link", routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
            node.put("image", adv.getImage());
            node.put("name", adv.getName());
            node.put("peopleCount", new AdventurerDAO().count(adv.getId()));
            node.put("favoritePlace", adv.getFavoritePlaceId() != null ? Json.toJson(new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId())) : null);
            node.put("favoriteTime", adv.getFavoriteTimeId() != null ? Json.toJson(new TimeOptionDAO().get(adv.getId(), adv.getFavoriteTimeId())) : null);
            node.put("lat", adv.getFavoritePlaceId() != null ? new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId()).getLatitude().floatValue() : 0F);
            node.put("lng", adv.getFavoritePlaceId() != null ? new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId()).getLongitude().floatValue() : 0F);

            result.add(node);
        }

        return ok(Json.toJson(result));
    }

    public static Result getPublicAdventuresOfCategory(String catId) {
        DynamicForm data = form().bindFromRequest();
        String lastId = data.get("lastId");
        int count = new Integer(data.get("count")).intValue();

        if (Cache.get("category." + catId + ".publicadventures." + lastId + "." + count) != null)
            return ok(Json.toJson(Cache.get("category." + catId + ".publicadventures." + lastId + "." + count)));


        List<ObjectNode> result = new ArrayList<ObjectNode>(count);

        boolean more = true;
        if (count > 0)
            while (more) {
                List<AdventureCategory> advCats = new AdventureCategoryDAO().all(catId, lastId, count);
                more = advCats.size() > 0;
                for (AdventureCategory advCat : advCats) {
                    if (more) {
                        Adventure adv = new AdventureDAO().get(advCat.getAdventureId());
                        if (adv != null && adv.isPublish()) {
                            ObjectNode node = Json.newObject();
                            node.put("id", adv.getId());
                            node.put("link", routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
                            node.put("image", adv.getImage());
                            node.put("name", adv.getName());
                            node.put("peopleCount", new AdventurerDAO().count(adv.getId()));
                            node.put("favoritePlace", adv.getFavoritePlaceId() != null ? Json.toJson(new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId())) : null);
                            node.put("favoriteTime", adv.getFavoriteTimeId() != null ? Json.toJson(new TimeOptionDAO().get(adv.getId(), adv.getFavoriteTimeId())) : null);
                            node.put("lat", adv.getFavoritePlaceId() != null ? new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId()).getLatitude().floatValue() : 0F);
                            node.put("lng", adv.getFavoritePlaceId() != null ? new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId()).getLongitude().floatValue() : 0F);

                            result.add(node);

                            more = result.size() < count;
                        }
                        lastId = adv.getId();
                    }
                }

            }


        Cache.set("category." + catId + ".publicadventures." + lastId + "." + count, result);


        return ok(Json.toJson(result));
    }


    public static Result getInspirations(String catId) {
        DynamicForm data = form().bindFromRequest();
        String lastId = data.get("lastId");
        int count = new Integer(data.get("count")).intValue();

        if (Cache.get("category." + catId + ".inspirations." + lastId + "." + count) != null)
            return ok(Json.toJson(Cache.get("category." + catId + ".inspirations." + lastId + "." + count)));

        Date now = new Date();

        List<ObjectNode> result = new ArrayList<ObjectNode>();

        boolean more = true;
        if (count > 0)
            while (more) {
                List<InspirationCategory> all = new InspirationCategoryDAO().all(catId, lastId, count);
                more = all.size() > 0;
                for (InspirationCategory insCat : all) {
                    if (insCat.getInspirationId() != null) {
                        Inspiration ins = new InspirationDAO().get(insCat.getInspirationId());
                        if (ins != null && (ins.getTimeEnd() == null || ins.getTimeEnd().after(now))) {
                            ObjectNode node = Json.newObject();
                            node.put("id", ins.getId());
                            node.put("link", routes.InspirationController.get(ins.getId()).absoluteURL(request()));
                            node.put("image", ins.getImage());
                            node.put("name", ins.getName());
                            node.put("lat", ins.getPlaceLatitude() != null ? ins.getPlaceLatitude().floatValue() : 0F);
                            node.put("lng", ins.getPlaceLongitude() != null ? ins.getPlaceLongitude().floatValue() : 0F);

                            result.add(node);
                            more = result.size() < count;
                        }
                        lastId = insCat.getInspirationId();
                    }
                }
            }
        Cache.set("category." + catId + ".inspirations." + lastId + "." + count, result);

        return ok(Json.toJson(result));
    }


    public static Result subscribe() {
        Form<Subscriber> filledSubForm = subForm.bindFromRequest();
        Subscriber sub = filledSubForm.get();
        if (new SubscriberDAO().save(sub))
            flash("success", "You are subscribed now! We'll let you know.");
        else
            flash("error", "You could not be subscribed :(");

        try {
            ListSubscribeMethod listSubscribeMethod = new ListSubscribeMethod();
            listSubscribeMethod.apikey = "426c4fc75113db8416df74f92831d066-us4";
            listSubscribeMethod.id = "c18d5a32fb";
            listSubscribeMethod.email_address = sub.getEmail();
            listSubscribeMethod.double_optin = false;
            listSubscribeMethod.update_existing = true;
            listSubscribeMethod.send_welcome = true;

            new MailChimpClient().execute(listSubscribeMethod);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MailChimpException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return ok(subscribe.render(subForm));
    }


    @Cached(key = "imprint")
    public static Result imprint() {
        return ok(imprint.render());
    }

    @Cached(key = "about")
    public static Result about() {
        return ok(about.render());
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result admin() {
        return ok(admin.render());
    }

    public static Result changeLanguage(String lang) {
        changeLang(lang);

        return redirect(request().getHeader(REFERER) != null ? request().getHeader(REFERER) : "/");
    }

    public static Result oAuthDenied(String provider) {
        return ok("oAuth went wrong");
    }

    @Cached(key = "ping")
    public static Result ping() {
        return ok("pong");
    }
}
