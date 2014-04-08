package controllers.api.json;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.AuthInfo;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthInfo;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthProvider;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import models.mobile.EPlatform;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mugglmenzel on 01/04/14.
 */
public class MobileAppController extends Controller {

    public static Result getLoginToken() {
        DynamicForm form = Form.form().bindFromRequest();
        String authProvider = form.get("authProvider");
        String authUserId = form.get("authUserId");
        String newToken = form.get("accessToken");
        Long expires = form.get("expires") != null ? Long.parseLong(form.get("expires")) : 0L;
        String mobileAppSecret = form.get("mobileAppSecret");
        EPlatform platform = form.get("mobilePlatform") != null && !"".equals(form.get("mobilePlatform")) ? EPlatform.valueOf(form.get("mobilePlatform")) : null;

        Logger.debug("Got following form items: " + form.toString());
        Logger.debug("same as map: " + form.data());

        AuthUser au = PlayAuthenticate.getProvider(authProvider).getSessionAuthUser(authUserId, expires.longValue());
        PlayAuthenticate.storeUser(ctx().session(), au);

        return ok();
    }

}
