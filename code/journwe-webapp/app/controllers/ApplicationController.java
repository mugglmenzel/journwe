package controllers;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import controllers.auth.SecuredAdminUser;
import models.dao.AdventureDAO;
import models.dao.CategoryDAO;
import models.dao.InspirationDAO;
import models.dao.SubscriberDAO;
import models.Category;
import models.Subscriber;
import models.helpers.CategoryCount;
import play.cache.Cached;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
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
                && SecuredAdminUser.isAdmin(usr)) {
            List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
            for (Category cat : new CategoryDAO().all(10))
                catCounts.add(new CategoryCount(cat, new CategoryDAO()
                        .countInspirations(cat.getId())));
            return ok(index.render(catCounts, new InspirationDAO().all(50), new AdventureDAO().all(50),
                    null));
        } else {
            return ok(subscribe.render(subForm));
        }
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


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result categoryIndex(String catId) {
        List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
        for (Category cat : new CategoryDAO().all(10))
            catCounts.add(new CategoryCount(cat, new CategoryDAO()
                    .countInspirations(cat.getId())));
        return ok(index.render(catCounts, new InspirationDAO().all(50, catId), new AdventureDAO().all(50),
                catId));
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


    public static Result oAuthDenied(String provider) {
        return ok("oAuth went wrong");
    }

    @Cached(key = "ping")
    public static Result ping() {
        return ok("pong");
    }
}
