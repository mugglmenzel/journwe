package controllers.html;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.UserManager;
import models.auth.SecuredUser;
import models.category.Category;
import models.dao.adventure.AdventurerDAO;
import models.dao.category.CategoryDAO;
import models.user.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Created by mugglmenzel on 22/02/14.
 */
public class IndexController extends Controller {

    public static Result index() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
                && SecuredUser.isAuthorized(usr)) {
            User user = UserManager.findByAuthUserIdentity(usr);
            if (new AdventurerDAO().isAdventurer(user.getId())) return Results.ok(views.html.index.indexVet.render(user));
            else return Results.ok(views.html.index.index.render(user));

        } else return Results.ok(views.html.index.landing.render());

    }


    public static Result indexNew() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
                && SecuredUser.isAuthorized(usr)) {
            User user = UserManager.findByAuthUserIdentity(usr);
            return Results.ok(views.html.index.index.render(user));
        } else {
            flash("info", "Please login to start a JournWe.");
            return Results.ok(views.html.index.landing.render());
        }
    }

    public static Result categoryOverview() {
        return categoryIndex(null);
    }

    public static Result categoryIndex(String catId) {
        if (catId == null || "".equals(catId)) catId = Category.SUPER_CATEGORY;

        return Results.ok(views.html.index.indexCat.render(new CategoryDAO().get(catId)));
    }
}
