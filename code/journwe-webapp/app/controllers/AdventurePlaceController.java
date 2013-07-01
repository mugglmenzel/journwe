package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import controllers.auth.SecuredAdminUser;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.place.PlaceOption;
import models.dao.*;
import models.user.User;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.getTodos;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.06.13
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class AdventurePlaceController extends Controller {

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getPlaces(String advId) {
        return ok(Json.toJson(new PlaceOptionDAO().all(advId)));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result addPlace(String advId) {

        DynamicForm requestData = form().bindFromRequest();

        PlaceOption place = new PlaceOption();
        place.setAdventureId(advId);
        place.setGoogleMapsAddress(requestData.get("googleMapsAddress"));
        place.setName(requestData.get("name"));

        new PlaceOptionDAO().save(place);

        return ok(Json.toJson(place));
    }

}
