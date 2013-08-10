package controllers;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.Inspiration;
import models.auth.SecuredBetaUser;
import models.category.Category;
import models.adventure.Adventure;
import models.dao.*;
import models.helpers.CategoryCount;
import models.user.Subscriber;
import org.codehaus.jackson.node.ObjectNode;
import play.cache.Cached;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import views.html.index.*;
import views.html.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;


public class ApplicationController extends Controller {

    private static Form<Subscriber> subForm = form(Subscriber.class);

    public static Result index() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
                && (SecuredBetaUser.isBeta(usr) || SecuredBetaUser.isAdmin(usr))) {

            String userId = new UserDAO().findByAuthUserIdentity(usr).getId();

            if (new AdventurerDAO().isAdventurer(userId))
                return ok(indexVet.render());
            else {

                return ok(index.render());
            }
        } else {
            return ok(subscribe.render(subForm));
        }
    }

    public static Result indexNew() {
        List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
        for (Category cat : new CategoryDAO().all())
            catCounts.add(new CategoryCount(cat, new CategoryDAO()
                    .countInspirations(cat.getId())));

        return ok(index.render());
    }


    public static Result categoryIndex(String catId) {

        return ok(indexCat.render(new CategoryDAO().get(catId)));
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

        List<ObjectNode> result = new ArrayList<ObjectNode>();
        for (Adventure adv : new AdventureDAO().allPublic(lastId, count)) {
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


    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getInspirations(String catId) {
        DynamicForm data = form().bindFromRequest();
        String lastId = data.get("lastId");
        int count = new Integer(data.get("count")).intValue();

        List<ObjectNode> result = new ArrayList<ObjectNode>();
        for (Inspiration ins : new InspirationDAO().all(catId, lastId, count)) {
            ObjectNode node = Json.newObject();
            node.put("id", ins.getInspirationId());
            node.put("link", routes.InspirationController.get(ins.getCategoryId(), ins.getInspirationId()).absoluteURL(request()));
            node.put("image", ins.getImage());
            node.put("name", ins.getName());

            result.add(node);
        }

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

    @Security.Authenticated(SecuredBetaUser.class)
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
