package controllers.core.html;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.typesafe.config.ConfigFactory;
import models.UserManager;
import models.cache.CachedUserDAO;
import models.contact.Contact;
import models.dao.SubscriberDAO;
import models.user.Subscriber;
import models.user.User;
import play.cache.Cache;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import models.providers.MyUsernamePasswordAuthProvider;
import views.html.*;

import java.io.IOException;

import static play.data.Form.form;


public class ApplicationController extends Controller {



    public static Result index() {
        return ok(views.html.index.landing.render());
    }


    public static Result imprint() {
        return Results.ok(imprint.render());
    }

    public static Result about() {
        return Results.ok(about.render());
    }

    public static Result contact() {
        return Results.ok(contact.render(new Form(Contact.class)));
    }

    public static Result contactForm() {
        Form<Contact> form = new Form(Contact.class).bindFromRequest();
        if (!form.hasErrors()) {
            Contact ct = form.get();
            new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                    ConfigFactory.load().getString("aws.accessKey"),
                    ConfigFactory.load().getString("aws.secretKey"))).sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses("info@journwe-company.com")).withSource("contact@journwe.com").withReplyToAddresses(ct.getName() + "<" + ct.getEmail() + ">").withMessage(new Message().withSubject(new Content().withData(ct.getSubject())).withBody(new Body().withText(new Content().withData(ct.getText() + "\n\nName: " + ct.getName())))));
            flash("success", "Your Email has been sent. We'll contact you shortly.");
            return redirect(controllers.core.html.routes.ApplicationController.contact());
        } else
            return Results.ok(contact.render(form));

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
        new CachedUserDAO().clearCache(userId);
    }

    // For JournWe non-thirdparty signup and login

    public static Result login() {
        return Results.ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
    }

    public static Result doLogin() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MyUsernamePasswordAuthProvider.MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
// User did not fill everything properly
            return Results.badRequest(login.render(filledForm));
        } else {
// Everything was filled
            return UsernamePasswordAuthProvider.handleLogin(ctx());
        }
    }

    public static Result doLogout() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())) {
            User user = UserManager.findByAuthUserIdentity(usr);
            if (user != null) clearUserCache(user.getId());
        }

        return com.feth.play.module.pa.controllers.Authenticate.logout();
    }


    public static Result signup() {
        return Results.ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
    }

    public static Result doSignup() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MyUsernamePasswordAuthProvider.MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
// User did not fill everything properly
            return Results.badRequest(signup.render(filledForm));
        } else {
// Everything was filled
// do something with your part of the form before handling the user
// signup
            return MyUsernamePasswordAuthProvider.handleSignup(ctx());
        }
    }


    public static Result subscribe() {
        Form<Subscriber> filledSubForm = form(Subscriber.class).bindFromRequest();
        Subscriber sub = filledSubForm.get();
        if (new SubscriberDAO().save(sub)) {
            flash("success", "You are subscribed now! We keep you posted.");
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
                e.printStackTrace();
            } catch (MailChimpException e) {
                e.printStackTrace();
            }

        } else
            flash("error", "You could not be subscribed :(");



        return redirect(controllers.core.html.routes.ApplicationController.index());
    }

}
