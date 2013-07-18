package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import models.auth.SecuredBetaUser;
import models.adventure.Adventure;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.place.PlaceOption;
import models.dao.AdventureDAO;
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

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.06.13
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class AdventurePlaceController extends Controller {

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getPlaces(String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        List<ObjectNode> places = new ArrayList<ObjectNode>();
        for (PlaceOption po : new PlaceOptionDAO().all(advId)) {
            ObjectNode node = Json.newObject();
            node.put("id", po.getOptionId());
            node.put("placeId", po.getPlaceId());
            node.put("advId", po.getAdventureId());
            node.put("address", po.getAddress());
            PlaceAdventurerPreference pref = new PlaceAdventurerPreferenceDAO().get(po.getOptionId(), usr.getId());
            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
            node.put("voteCount", Json.toJson(new PlaceAdventurerPreferenceDAO().counts(po.getPlaceId(), po.getPlaceId())));
            node.put("voteAdventurers", Json.toJson(new PlaceAdventurerPreferenceDAO().adventurersNames(po.getPlaceId(), po.getPlaceId())));

            places.add(node);
        }

        return ok(Json.toJson(places));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getFavoritePlace(String advId) {
        if (new PlaceOptionDAO().count(advId) < 1) return ok(Json.toJson(""));

        String favId = new AdventureDAO().get(advId).getFavoritePlaceId();

        return favId != null ? ok(Json.toJson(new PlaceOptionDAO().get(advId, favId))) : ok(Json.toJson(""));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result setFavoritePlace(String advId) {
        DynamicForm data = form().bindFromRequest();
        String favId = data.get("favoritePlaceId");
        Adventure adv = new AdventureDAO().get(advId);
        adv.setFavoritePlaceId(favId);
        new AdventureDAO().save(adv);

        return ok(Json.toJson(new PlaceOptionDAO().get(advId, favId)));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result addPlace(String advId) {

        DynamicForm requestData = form().bindFromRequest();

        PlaceOption place = new PlaceOption();
        place.setAdventureId(advId);
        place.setAddress(requestData.get("address"));

        new PlaceOptionDAO().save(place);
        Logger.debug("saved placeoption");

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        PlaceAdventurerPreference pref = new PlaceAdventurerPreference();
        pref.setPlaceOptionId(place.getOptionId());
        pref.setAdventurerId(usr.getId());
        new PlaceAdventurerPreferenceDAO().save(pref);
        Logger.debug("saved placeoptionpreference");


        ObjectNode node = Json.newObject();
        node.put("id", place.getOptionId());
        node.put("placeId", place.getPlaceId());
        node.put("advId", place.getAdventureId());
        node.put("address", place.getAddress());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteCount", Json.toJson(new PlaceAdventurerPreferenceDAO().counts(place.getPlaceId(), place.getPlaceId())));
        node.put("voteAdventurers", Json.toJson(new PlaceAdventurerPreferenceDAO().adventurersNames(place.getPlaceId(), place.getPlaceId())));
        Logger.debug("created json node");

        return ok(Json.toJson(node));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result vote(String advId, String placeId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        PlaceOption place = new PlaceOptionDAO().get(advId, placeId);
        String vote = form().bindFromRequest().get("vote").toUpperCase();

        PlaceAdventurerPreference pref = new PlaceAdventurerPreferenceDAO().get(place.getOptionId(), usr.getId());
        if (pref == null) {
            pref = new PlaceAdventurerPreference();
            pref.setPlaceOptionId(place.getOptionId());
            pref.setAdventurerId(usr.getId());
        }

        try {
            pref.setVote(EPreferenceVote.valueOf(vote));
        } catch (IllegalArgumentException e) {
            Logger.error("Got unknown value for vote! value: " + vote);
        }

        new PlaceAdventurerPreferenceDAO().save(pref);

        //PlaceOption place = PlaceOption.fromId(pref.getPlaceOptionId());
        //place = new PlaceOptionDAO().get(place.getAdventureId(), place.getAddress());

        ObjectNode node = Json.newObject();
        node.put("id", place.getOptionId());
        node.put("advId", place.getAdventureId());
        node.put("placeId", place.getPlaceId());
        node.put("address", place.getAddress());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteCount", Json.toJson(new PlaceAdventurerPreferenceDAO().counts(place.getAdventureId(), place.getPlaceId())));
        node.put("voteAdventurers", Json.toJson(new PlaceAdventurerPreferenceDAO().adventurersNames(place.getPlaceId(), place.getPlaceId())));

        return ok(Json.toJson(node));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result deletePlace(String advId, String placeId) {
        new PlaceOptionDAO().delete(advId, placeId);
        return ok();
    }

}
