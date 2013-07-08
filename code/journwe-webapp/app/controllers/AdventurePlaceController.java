package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import controllers.auth.SecuredAdminUser;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.place.PlaceOption;
import models.dao.PlaceAdventurerPreferenceDAO;
import models.dao.PlaceOptionDAO;
import models.dao.UserDAO;
import models.user.User;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        for (PlaceOption po : new PlaceOptionDAO().all(advId)) {
            ObjectNode node = Json.newObject();
            node.put("id", po.getId());
            node.put("address", po.getGoogleMapsAddress());
            PlaceAdventurerPreference pref = new PlaceAdventurerPreferenceDAO().get(po.getId(), usr.getId());
            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
            node.put("voteCount", Json.toJson(new PlaceAdventurerPreferenceDAO().counts(po.getId())));
            node.put("voteAdventurers", Json.toJson(new PlaceAdventurerPreferenceDAO().adventurersNames(po.getId())));

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

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        PlaceAdventurerPreference pref = new PlaceAdventurerPreference();
        pref.setPlaceOptionId(place.getId());
        pref.setAdventurerId(usr.getId());
        new PlaceAdventurerPreferenceDAO().save(pref);


        ObjectNode node = Json.newObject();
        node.put("id", place.getId());
        node.put("address", place.getGoogleMapsAddress());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteCount", Json.toJson(new PlaceAdventurerPreferenceDAO().counts(place.getId())));
        node.put("voteAdventurers", Json.toJson(new PlaceAdventurerPreferenceDAO().adventurersNames(place.getId())));

        return ok(Json.toJson(node));
    }

    public static Result vote(String optId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        String vote = form().bindFromRequest().get("vote").toUpperCase();

        PlaceAdventurerPreference pref = new PlaceAdventurerPreferenceDAO().get(optId, usr.getId());
        if (pref == null) {
            pref = new PlaceAdventurerPreference();
            pref.setPlaceOptionId(optId);
            pref.setAdventurerId(usr.getId());
        }

        try {
            pref.setVote(EPreferenceVote.valueOf(vote));
        } catch (IllegalArgumentException e) {
            Logger.error("Got unknown value for vote! value: " + vote);
        }

        new PlaceAdventurerPreferenceDAO().save(pref);

        PlaceOption place = new PlaceOptionDAO().get(pref.getPlaceOptionId());

        ObjectNode node = Json.newObject();
        node.put("id", place.getId());
        node.put("address", place.getGoogleMapsAddress());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteCount", Json.toJson(new PlaceAdventurerPreferenceDAO().counts(place.getId())));
        node.put("voteAdventurers", Json.toJson(new PlaceAdventurerPreferenceDAO().adventurersNames(place.getId())));

        return ok(Json.toJson(node));
    }

    public static Result deletePlace(String optId) {
        new PlaceOptionDAO().delete(optId);
        return ok();
    }

}