package controllers.admin.html;

import models.auth.SecuredAdminUser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import views.html.adminIndex;

/**
 * Created by mugglmenzel on 22/02/14.
 */
public class AdminController extends Controller {
    @Security.Authenticated(SecuredAdminUser.class)
    public static Result admin() {
        return Results.ok(adminIndex.render());
    }
}
