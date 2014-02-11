package controllers.html;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.restfb.json.JsonObject;
import models.auth.SecuredUser;
import models.dao.user.UserSocialDAO;
import models.helpers.JournweFacebookChatClient;
import models.helpers.JournweFacebookClient;
import models.user.UserSocial;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: markus
 * Date: 7/10/13
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FacebookController extends Controller {

    @Security.Authenticated(SecuredUser.class)
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
//        // Test #7 post a link with caption, description and text message on the wall
        fb.publishLinkOnMyFeed("Message #1", "http://www.faz.net", "www.url-1.de", "Description #1", "http://www.thebushcraftstore.co.uk/ekmps/shops/bduimportsltd/images/swedish-forest-bushcraft-knife-choice-of-handles-on-sale--[2]-10816-p.jpg");
        // Test #8 Post only a link
        fb.publishLinkOnMyFeed("http://www.spiegel.de");
        // Test #9 Post only a text message
        fb.publishMessageOnMyFeed("Message #2");
        Logger.debug("+++ END TESTING FACEBOOK FEATURES +++");
        return ok();
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result sendMessage() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        final String accessToken = us.getAccessToken();

        DynamicForm requestData = form().bindFromRequest();
        String destinationUser = requestData.get("destinationUser");
        String messageText = requestData.get("messageText");

        JournweFacebookChatClient fbchat = new JournweFacebookChatClient();
        fbchat.sendMessage(accessToken,messageText,destinationUser);
        return ok();
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result createMessage() {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        final String accessToken = us.getAccessToken();
        JournweFacebookClient fb = JournweFacebookClient.create(accessToken);
        List<JsonObject> friends = fb.getMyFriendsAsJson();
        Logger.debug("Friends as JSON: " + friends.toString());
        StringBuffer friendNames = new StringBuffer("[");
        int i = 0;
        for (JsonObject jo : friends) {
            // append friend name to array
            friendNames.append("{label: \"");
            friendNames.append(jo.get("name"));
            friendNames.append("\"");
            friendNames.append(", value: \"");
            friendNames.append(jo.get("id"));
            friendNames.append("\"}");
            if (i < (friends.size() - 1)) {
                friendNames.append(",");
            }
            i++;
        }
        friendNames.append("]");
        Logger.debug("Friend names as JSON: " + friendNames);
        return ok(views.html.tests.test_invite.render(friendNames.toString()));
    }
}
