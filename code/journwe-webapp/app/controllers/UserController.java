package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.Adventure;
import models.auth.SecuredBetaUser;
import models.dao.*;
import models.notifications.ENotificationFrequency;
import models.notifications.Notification;
import models.user.User;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user.get;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 26.07.13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public class UserController extends Controller {

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getProfile(String userId) {
        return ok(get.render(new UserDAO().get(userId)));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result setMailDigestFrequency(String userId, String frequency) {
        ENotificationFrequency freq = ENotificationFrequency.valueOf(frequency);
        if (freq != null) {
            User usr = new UserDAO().get(userId);
            usr.setNotificationDigest(freq);
            new UserDAO().save(usr);
            return ok(Json.toJson(usr.getNotificationDigest()));
        }
        return badRequest();
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getNotifications() {
        DynamicForm data = form().bindFromRequest();
        String lastId = data.get("lastId");
        int count = new Integer(data.get("count")).intValue();

        Logger.debug("getting notifications for " + lastId + "," + count);

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        List<JsonNode> results = new ArrayList<JsonNode>();
        List<Notification> all = new NotificationDAO().all(usr.getId(), lastId, count);
        for (Notification c : all) {
            ObjectNode node = Json.newObject();

            String image = null;
            switch (c.getTopic()) {
                case ADVENTURE:
                    image = c.getTopicRef() != null ? new AdventureDAO().get(c.getTopicRef()).getImage() : "";
                    break;
                case GENERAL:
                case USER:
                default:
                    image = routes.UserController.getProfile(usr.getId()).absoluteURL(request());
                    break;
            }
            node.put("image", image);

            String link = "#";
            switch (c.getTopic()) {
                case ADVENTURE:
                    link = routes.AdventureController.getIndex(c.getTopicRef()).absoluteURL(request());
                    break;
                case GENERAL:
                case USER:
                default:
                    link = routes.UserController.getProfile(usr.getId()).absoluteURL(request());
                    break;
            }
            node.put("link", link);
            node.put("subject", c.getSubject());
            node.put("message", c.getMessage());
            node.put("read", c.isRead());
            node.put("sent", c.isSent());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            node.put("created", sdf.format(c.getCreated()));
            results.add(node);

            c.setRead(true);
            new NotificationDAO().save(c);
        }

        return ok(Json.toJson(results));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getAdventures(String userId) {
        DynamicForm data = form().bindFromRequest();
        int count = new Integer(data.get("count")).intValue();


        List<ObjectNode> results = new ArrayList<ObjectNode>();
        for (Adventure adv : new AdventureDAO().allOfUserId(userId, null, count)) {
            ObjectNode node = Json.newObject();
            node.put("id", adv.getId());
            node.put("link", routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
            node.put("image", adv.getImage());
            node.put("name", adv.getName());
            node.put("peopleCount", new AdventurerDAO().count(adv.getId()));
            node.put("favoritePlace", adv.getFavoritePlaceId() != null ? Json.toJson(new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId())) : null);
            node.put("favoriteTime", adv.getFavoriteTimeId() != null ? Json.toJson(new TimeOptionDAO().get(adv.getId(), adv.getFavoriteTimeId())) : null);

            results.add(node);
        }
        ObjectNode result = Json.newObject();
        result.put("adventures", Json.toJson(results));
        result.put("count", new AdventureDAO().countOfUserId(userId));

        return ok(Json.toJson(result));
    }
}
