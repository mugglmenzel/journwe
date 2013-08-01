package controllers;

import models.auth.SecuredBetaUser;
import models.dao.UserDAO;
import models.notifications.ENotificationFrequency;
import models.user.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user.get;

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
        if(freq != null) {
            User usr = new UserDAO().get(userId);
            usr.setNotificationDigest(freq);
            new UserDAO().save(usr);
            return ok(Json.toJson(usr.getNotificationDigest()));
        }
        return badRequest();
    }
}
