package controllers.html;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import controllers.routes;
import models.adventure.Adventure;
import models.auth.SecuredAdminUser;
import models.auth.SecuredUser;
import models.auth.SecuredUser;
import models.category.Category;
import models.category.CategoryCount;
import models.category.CategoryHierarchy;
import models.dao.*;
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
import models.user.EUserRole;
import models.user.Subscriber;
import models.user.User;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.Routes;
import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import providers.MyUsernamePasswordAuthProvider;
import views.html.*;
import views.html.index.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static play.data.Form.form;


public class ApplicationController extends Controller {

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    public static final String USER_ROLE = "user";

    public static Result index() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
                && SecuredUser.isAuthorized(usr)) {
            String userId = new UserDAO().findByAuthUserIdentity(usr).getId();
            if (new AdventurerDAO().isAdventurer(userId)) return ok(indexVet.render());
            else return ok(index.render());

        } else return ok(landing.render());

    }

    public static Result indexNew() {
        return ok(index.render());
    }

    public static Result categoryIndex(String catId) {
        if (Category.SUPER_CATEGORY.equals(catId)) return Results.redirect(controllers.html.routes.ApplicationController.index());
        return ok(indexCat.render(new CategoryDAO().get(catId)));
    }

    public static Result imprint() {
        return ok(imprint.render());
    }

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

    public static Result ping() {
        return ok("pong");
    }

    //BETA activation
    public static Result joinBeta() {
        if (!PlayAuthenticate.isLoggedIn(Http.Context.current().session())) {
            PlayAuthenticate.storeOriginalUrl(Http.Context.current());
            response().setCookie(UserDAO.COOKIE_USER_ROLE_ON_REGISTER, EUserRole.BETA.toString());
            return redirect(PlayAuthenticate.getProvider("facebook").getUrl());
        } else {
            User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
            usr.setActive(true);
            usr.setRole(EUserRole.BETA);
            new UserDAO().save(usr);

            return ok(index.render());
        }
    }

    public static void clearUserCache(final String userId) {
        Cache.remove("user." + userId + ".myadventures");
    }

    // For JournWe non-thirdparty signup and login

    public static Result login() {
        return ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
    }

    public static Result doLogin() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MyUsernamePasswordAuthProvider.MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
// User did not fill everything properly
            return badRequest(login.render(filledForm));
        } else {
// Everything was filled
            return UsernamePasswordAuthProvider.handleLogin(ctx());
        }
    }

    public static Result signup() {
        return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
    }

    public static Result doSignup() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MyUsernamePasswordAuthProvider.MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
// User did not fill everything properly
            return badRequest(signup.render(filledForm));
        } else {
// Everything was filled
// do something with your part of the form before handling the user
// signup
            return UsernamePasswordAuthProvider.handleSignup(ctx());
        }
    }

    /**
     * Returns a list of all routes to handle in the javascript
     */
    public static Result routes() {
        response().setContentType("text/javascript");
        return ok("define(function(){" + // Make it AMD compatible
                Routes.javascriptRouter("routes",
                        controllers.api.json.routes.javascript.AdventureController.updateImage(),
                        controllers.api.json.routes.javascript.AdventureController.updatePlaceVoteDeadline(),
                        controllers.api.json.routes.javascript.AdventureController.updateTimeVoteDeadline(),
                        controllers.api.json.routes.javascript.AdventureController.updatePlaceVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureController.updateTimeVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureController.placeVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureEmailController.listEmails(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.getPlaces(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.getFavoritePlace(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.setFavoritePlace(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.addPlace(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.voteParam(),
                        controllers.api.json.routes.javascript.AdventurePlaceController.deletePlace(),
                        controllers.api.json.routes.javascript.AdventureTimeController.getTimes(),
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
                        controllers.api.json.routes.javascript.AdventureFileController.deleteFile()

                ) + ";; return routes;});"
        );
    }
}
