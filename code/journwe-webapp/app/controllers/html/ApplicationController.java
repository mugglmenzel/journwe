package controllers.html;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.typesafe.config.ConfigFactory;
import models.admin.Contact;
import models.auth.SecuredAdminUser;
import models.auth.SecuredUser;
import models.category.Category;
import models.dao.adventure.AdventurerDAO;
import models.dao.category.CategoryDAO;
import models.dao.user.UserDAO;
import models.user.User;
import org.reflections.Reflections;
import play.Routes;
import play.api.Play;
import play.cache.Cache;
import play.core.Router;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import providers.MyUsernamePasswordAuthProvider;
import views.html.*;
import views.html.about;
import views.html.admin;
import views.html.contact;
import views.html.imprint;
import views.html.index.index;
import views.html.index.indexCat;
import views.html.index.indexVet;
import views.html.index.landing;
import views.html.login;
import views.html.signup;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;


public class ApplicationController extends Controller {

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    public static final String USER_ROLE = "user";

    public static Result index() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
                && SecuredUser.isAuthorized(usr)) {
            User user = new UserDAO().findByAuthUserIdentity(usr);
            if (new AdventurerDAO().isAdventurer(user.getId())) return ok(indexVet.render(user));
            else return ok(index.render(user));

        } else return ok(landing.render());

    }


    public static Result indexNew() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
                && SecuredUser.isAuthorized(usr)) {
            User user = new UserDAO().findByAuthUserIdentity(usr);
            return ok(index.render(user));
        } else {
            flash("info", "Please login to start a JournWe.");
            return ok(landing.render());
        }
    }

    public static Result categoryOverview() {
        return categoryIndex(null);
    }

    public static Result categoryIndex(String catId) {
        if (catId == null || "".equals(catId)) catId = Category.SUPER_CATEGORY;

        return ok(indexCat.render(new CategoryDAO().get(catId)));
    }

    public static Result imprint() {
        return ok(imprint.render());
    }

    public static Result about() {
        return ok(about.render());
    }

    public static Result contact() {
        return ok(contact.render(new Form(Contact.class)));
    }

    public static Result contactForm() {
        Form<Contact> form = new Form(Contact.class).bindFromRequest();
        if (!form.hasErrors()) {
            Contact ct = form.get();
            new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                    ConfigFactory.load().getString("aws.accessKey"),
                    ConfigFactory.load().getString("aws.secretKey"))).sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses("info@journwe-company.com")).withSource("contact@journwe.com").withReplyToAddresses(ct.getName() + "<" + ct.getEmail() + ">").withMessage(new Message().withSubject(new Content().withData(ct.getSubject())).withBody(new Body().withText(new Content().withData(ct.getText() + "\n\nName: " + ct.getName())))));
            flash("success", "Your Email has been sent. We'll contact you shortly.");
            return redirect(routes.ApplicationController.contact());
        } else
            return ok(contact.render(form));

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

    /*
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

            return ok(index.render(usr));
        }
    }
    */

    public static void clearUserCache(final String userId) {
        Cache.remove("user." + userId + ".myadventures");
        new UserDAO().clearCache(userId);
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

    public static Result doLogout() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())) {
            User user = new UserDAO().findByAuthUserIdentity(usr);
            if (user != null) clearUserCache(user.getId());
        }

        return com.feth.play.module.pa.controllers.Authenticate.logout();
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


    public static Result messages() {
        response().setContentType("text/javascript");
        return ok("define(function(){ var messages = " + Json.toJson(scala.collection.JavaConversions.asJavaMap(play.api.i18n.Messages.messages(Play.current()).get(Http.Context.current().lang().code()).get())).toString() + "; return messages;});");
    }


    /**
     * Returns a list of all routes to handle in the javascript
     */
    public static Result routes() {
        response().setContentType("text/javascript");

        /*
        Set<play.core.Router.JavascriptReverseRoute> routes = new HashSet<play.core.Router.JavascriptReverseRoute>();


        Reflections reflections = new Reflections("controllers.api.json.javascript");
        for(Class<? extends Object> clazz : reflections.getSubTypesOf(Object.class))
            for(Method met : clazz.getMethods())
                     routes.add(new Router.JavascriptReverseRoute("controllers.api.json", met.getName()));
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
                        controllers.api.json.routes.javascript.AdventureController.updatePlaceVoteDeadline(),
                        controllers.api.json.routes.javascript.AdventureController.updateTimeVoteDeadline(),
                        controllers.api.json.routes.javascript.AdventureController.updatePlaceVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureController.updateTimeVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureController.placeVoteOpen(),
                        controllers.api.json.routes.javascript.AdventureController.updateCategory(),
                        controllers.api.json.routes.javascript.AdventureController.updatePublic(),
                        controllers.api.json.routes.javascript.AdventureEmailController.listEmails(),
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
                        controllers.api.json.routes.javascript.CommentController.listComments()
                ) + ";; return routes;});");
    }
}
