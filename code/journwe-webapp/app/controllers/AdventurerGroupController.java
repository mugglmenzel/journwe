package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.group.AdventurerGroup;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.route.RouteOption;
import models.auth.SecuredBetaUser;
import models.dao.*;
import models.user.User;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static play.data.Form.form;

public class AdventurerGroupController extends Controller {

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getGroups(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        List<AdventurerGroup> groups = new AdventurerGroupDAO().all(advId);
        if (advr == null)
            return badRequest("Sorry: You are not listed as an Adventurer in this Adventure.");
        else
            return ok(group.render(adv, advr, AdventureTimeController.timeForm, groups));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result createGroup(String advId) {
        AdventurerGroup group = createEmptyGroup(advId);
        new AdventurerGroupDAO().save(group);

        return ok("New group created!");
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result addGroup(String advId) {

        DynamicForm requestData = form().bindFromRequest();

        AdventurerGroup group = new AdventurerGroup();
        group.setAdventureId(advId);
        group.setGroupName(requestData.get("groupName"));
        group.setGroupDescription(requestData.get("groupDescription"));
        group.setRouteIds(Arrays.asList(requestData.get("routeIds").split(":")));

        new AdventurerGroupDAO().save(group);

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        PlaceAdventurerPreference pref = new PlaceAdventurerPreference();
        pref.setPlaceOptionId(place.getOptionId());
        pref.setAdventurerId(usr.getId());
        new PlaceAdventurerPreferenceDAO().save(pref);

        ObjectNode node = Json.newObject();
        node.put("id", place.getOptionId());
        node.put("placeId", place.getPlaceId());
        node.put("advId", place.getAdventureId());
        node.put("address", place.getAddress());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteCount", Json.toJson(new PlaceAdventurerPreferenceDAO().counts(place.getOptionId())));
        node.put("voteAdventurers", Json.toJson(new PlaceAdventurerPreferenceDAO().adventurersNames(place.getOptionId())));

        return ok(Json.toJson(node));
    }

    protected static AdventurerGroup createEmptyGroup(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        // Create and save an empty route
        RouteOption route = new RouteOption();
        new RouteOptionDAO().save(route);

        AdventurerGroup group = new AdventurerGroup();
        group.setAdventureId(advId);
        group.addAdventurerId(advr.getUserId());
        group.addRouteId(route.getRouteId());

        return group;
    }

}
