package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.restfb.json.JsonObject;
import controllers.auth.SecuredAdminUser;
import models.dao.UserSocialDAO;
import models.helpers.JournweFacebookChatClient;
import models.helpers.JournweFacebookClient;
import models.user.UserSocial;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: markus
 * Date: 7/10/13
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FacebookController extends Controller {

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
    public static Result testFacebookChat(String messageText, String destinationUser) {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        final String accessToken = us.getAccessToken();
        Logger.debug("+++ START TESTING FACEBOOK CHAT FEATURES +++");
        JournweFacebookChatClient fbchat = new JournweFacebookChatClient();
        fbchat.sendMessage(accessToken,messageText,destinationUser);
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
        return ok(views.html.test_invite.render(friendNames.toString()));
    }
}
