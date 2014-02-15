package controllers.html;

import models.auth.SecuredUser;
import models.dao.user.UserDAO;
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

    @Security.Authenticated(SecuredUser.class)
    public static Result getProfile(String userId) {
        return ok(get.render(new UserDAO().get(userId)));
    }

}
