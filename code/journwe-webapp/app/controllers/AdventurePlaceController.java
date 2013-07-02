package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import controllers.auth.SecuredAdminUser;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.place.PlaceOption;
import models.dao.*;
import models.user.User;
import org.codehaus.jackson.node.ObjectNode;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.getTodos;

import java.util.ArrayList;
import java.util.List;

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
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        List<ObjectNode> places = new ArrayList<ObjectNode>();
        for(PlaceOption po : new PlaceOptionDAO().all(advId)){
            ObjectNode node = Json.newObject();
            node.put("id", po.getId());
            node.put("address", po.getGoogleMapsAddress());
            PlaceAdventurerPreference pref = new PlaceAdventurerPreferenceDAO().get(po.getId(), usr.getId());
            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
            places.add(node);
        }
        return ok(Json.toJson(places));
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
