package controllers;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.restfb.json.JsonObject;
import com.typesafe.config.ConfigFactory;
import controllers.auth.SecuredAdminUser;
import models.Category;
import models.dao.*;
import models.helpers.CategoryCount;
import models.helpers.JournweFacebookChatClient;
import models.helpers.JournweFacebookClient;
import models.user.Subscriber;
import models.user.UserSocial;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import play.Logger;
import play.cache.Cached;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import views.html.*;

import static play.data.Form.form;

public class ApplicationController extends Controller {

    private static Form<Subscriber> subForm = form(Subscriber.class);

    public static Result index() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
                && SecuredAdminUser.isAdmin(usr)) {

            String userId = new UserDAO().findByAuthUserIdentity(usr).getId();
            List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
            for (Category cat : new CategoryDAO().all(10))
                catCounts.add(new CategoryCount(cat, new CategoryDAO()
                        .countInspirations(cat.getId())));

            if (new AdventurerDAO().isAdventurer(userId))
                return ok(indexVet.render(catCounts, new InspirationDAO().all(50), new AdventureDAO().allOfUserId(userId), null));
            else {

                return ok(index.render(catCounts, new InspirationDAO().all(50), new AdventureDAO().all(50),
                        null));
            }
        } else {
            return ok(subscribe.render(subForm));
        }
    }

    public static Result indexPublic() {
        List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
        for (Category cat : new CategoryDAO().all(10))
            catCounts.add(new CategoryCount(cat, new CategoryDAO()
                    .countInspirations(cat.getId())));

        return ok(index.render(catCounts, new InspirationDAO().all(50), new AdventureDAO().all(50),
                null));
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

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result testFacebookFeatures() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        final String accessToken = us.getAccessToken();
        Logger.debug("+++ START TESTING FACEBOOK FEATURES +++");
        Logger.debug("usr.getId() -> " + usr.getId());
        Logger.debug("Access Token: " + accessToken);
        JournweFacebookClient fb = JournweFacebookClient.create(accessToken);
        // Test #1 get my facebook user
        Logger.debug("My name: " + fb.getMyFacebookUser().getName());
        // Test #2 get my facebook user as json
        Logger.debug("My user as JSON: " + fb.getMyFacebookUserAsJson());
//        // Test #3 get my friends
//        Logger.debug("Some of my friends: ");
//        List<User> friends = fb.getMyFriends();
//        if(friends.size()>0) {
//        	Logger.debug("Friend #1: "+friends.get(0));
//            if(friends.size()>1)
//            	Logger.debug("Friend #2: "+friends.get(1));
//        } else {
//        	Logger.debug("Wow, dude. You have no friends.");
//        }
//        // Test #4 publish something on my feed
//        Logger.debug("I'm going to post something on your wall now... Muahahahaha!!1");
//        fb.publishOnMyFeed("Here is a random number for you: "+(new Random()).nextDouble());
//        // Test #5 create an event
//        Logger.debug("I will create an awesome event now.");
//        String eventId = fb.createNewEvent("Würstl grillen",
//    			"Innen kalt, außen schwarz - so muss das Würstchen aussehen.", "Auf dem Dach, Englerstr 11, Karlsruhe.",
//    			new Date(new Date().getTime()+100000000), new Date(new Date().getTime()+120000000));
//        // Test #6 invite people to the event
//        if(friends.size()>1) {
//        	Logger.debug("Inviting Friend #1: "+friends.get(0)+" and Friend #2: "+friends.get(1)+" to event "+eventId);
//        	List<String> theyAreInvited = new ArrayList<String>();
//        	theyAreInvited.add(friends.get(0).getId());
//        	theyAreInvited.add(friends.get(1).getId());
//        	fb.inviteFriends(eventId, theyAreInvited);
//        }
//        // Test #7 post a link with caption, description and text message on the wall
        fb.publishLinkOnMyFeed("Message #1", "http://www.faz.net", "www.url-1.de", "Description #1", "http://www.thebushcraftstore.co.uk/ekmps/shops/bduimportsltd/images/swedish-forest-bushcraft-knife-choice-of-handles-on-sale--[2]-10816-p.jpg");
        // Test #8 Post only a link
        fb.publishLinkOnMyFeed("http://www.spiegel.de");
        // Test #9 Post only a text message
        fb.publishMessageOnMyFeed("Message #2");
        Logger.debug("+++ END TESTING FACEBOOK FEATURES +++");
        return ok();
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result testFacebookChat() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        final String accessToken = us.getAccessToken();
        Logger.debug("+++ START TESTING FACEBOOK CHAT FEATURES +++");
        Logger.debug("usr.getId() -> " + usr.getId());
        Logger.debug("Access Token: " + accessToken);

        XMPPConnection connection = JournweFacebookChatClient.createXMPPConnection();
        try {
//            Logger.debug("#1: "+ConfigFactory.load().getString("play-authenticate/clientId"));
//            Logger.debug("#2: "+ConfigFactory.load("play-authenticate").getString("clientId"));
            connection.connect();
            connection.login("333105976811510", accessToken);

            String to = "100006056794571@chat.facebook.com";
            Chat chat = connection.getChatManager().createChat(to, null);
            chat.sendMessage("Message to "+to);
            to = "100006056794571@facebook.com";
            chat = connection.getChatManager().createChat(to, null);
            chat.sendMessage("Message to "+to);

            to = "100006080790124@chat.facebook.com";
            chat = connection.getChatManager().createChat(to, null);
            chat.sendMessage("Message to "+to);
        } catch (XMPPException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
        return ok();
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result testInvite() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        final String accessToken = us.getAccessToken();
        JournweFacebookClient fb = JournweFacebookClient.create(accessToken);
        List<JsonObject> friends = fb.getMyFriendsAsJson();
        Logger.debug("Friends as JSON: " + friends.toString());
        StringBuffer friendNames = new StringBuffer("[");
        int i = 0;
        for (JsonObject jo : friends) {
            friendNames.append("\"");
            friendNames.append(jo.get("name"));
            friendNames.append("\"");
            if (i < friends.size() - 1)
                friendNames.append(",");
        }
        friendNames.append("]");
        Logger.debug("Friend names as JSON: " + friendNames);
        return ok(test_invite.render(friendNames.toString()));
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
