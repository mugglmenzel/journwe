package controllers.html;

import com.feth.play.module.pa.PlayAuthenticate;
import controllers.routes;
import models.adventure.Adventure;
import models.auth.SecuredUser;
import models.dao.*;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.adventure.PlaceOptionDAO;
import models.dao.adventure.TimeOptionDAO;
import models.dao.user.UserDAO;
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

    @Security.Authenticated(SecuredUser.class)
    public static Result getProfile(String userId) {
        return ok(get.render(new UserDAO().get(userId)));
    }

}
