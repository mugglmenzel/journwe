package controllers.api.json;

import models.mobile.EPlatform;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mugglmenzel on 01/04/14.
 */
public class MobileAppController extends Controller {

    public static Result getLoginToken() {
        DynamicForm form = Form.form().bindFromRequest();
        String authProvider = form.get("authProvider");
        String authUserId = form.get("authUserId");
        String newToken = form.get("newOAuthToken");
        String mobileAppSecret = form.get("mobileAppSecret");
        EPlatform platform = EPlatform.valueOf(form.get("mobilePlatform"));

        return ok(Json.toJson("geheimercookie"));
    }

}
