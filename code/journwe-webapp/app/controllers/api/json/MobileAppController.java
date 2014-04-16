package controllers.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthInfo;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.providers.oauth2.google.GoogleAuthInfo;
import com.feth.play.module.pa.providers.oauth2.google.GoogleAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mugglmenzel on 01/04/14.
 */
public class MobileAppController extends Controller {

    public static Result authenticate(String authProvider) {

        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Expecting Json data");

        /*
        DynamicForm form = Form.form().bindFromRequest();
        String authProvider = form.get("authProvider");
        String authUserId = form.get("authUserId");
        String newToken = form.get("accessToken");
        Long expires = form.get("expires") != null ? Long.parseLong(form.get("expires")) : 0L;
        String mobileAppSecret = form.get("mobileAppSecret");
        EPlatform platform = form.get("mobilePlatform") != null && !"".equals(form.get("mobilePlatform")) ? EPlatform.valueOf(form.get("mobilePlatform")) : null;


        Logger.debug("Got following form items: " + form.toString());
        Logger.debug("same as map: " + form.data());
        */


        AuthUser au = null;

        if ("facebook".equals(authProvider)) {
            Map<String, String> authInfoMap = new HashMap<String, String>();
            authInfoMap.put(OAuth2AuthProvider.Constants.ACCESS_TOKEN, json.get(OAuth2AuthProvider.Constants.ACCESS_TOKEN).asText());
            authInfoMap.put("expires", json.get(OAuth2AuthProvider.Constants.EXPIRES_IN).asText());

            au = new FacebookAuthUser(json, new FacebookAuthInfo(authInfoMap), null);
        } else if ("google".equals(authProvider)) {
            au = new GoogleAuthUser(json, new GoogleAuthInfo(json), null);
        }
        //AuthUser au = PlayAuthenticate.getProvider(authProvider).getSessionAuthUser(authUserId, expires.longValue());
        if (PlayAuthenticate.getUserService().getLocalIdentity(au) == null)
            PlayAuthenticate.getUserService().save(au);

        PlayAuthenticate.storeUser(ctx().session(), au);

        return ok();
    }

}
