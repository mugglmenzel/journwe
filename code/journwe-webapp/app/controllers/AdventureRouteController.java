package controllers;

import models.adventure.Adventure;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.place.PlaceOption;
import models.adventure.route.RouteOption;
import models.auth.SecuredBetaUser;
import models.dao.AdventureDAO;
import models.dao.PlaceAdventurerPreferenceDAO;
import models.dao.PlaceOptionDAO;
import models.dao.RouteOptionDAO;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;

public class AdventureRouteController extends Controller {

//    @Security.Authenticated(SecuredBetaUser.class)
//    public static Result addRoute(String advId) {
//
//    }

//    @Security.Authenticated(SecuredBetaUser.class)
//    public static Result getRoutes(String advId) {
//        Adventure adv = new AdventureDAO().get(advId);
//        List<ObjectNode> routes = new ArrayList<ObjectNode>();
//        for (RouteOption ro : new RouteOptionDAO().all(advId)) {
//            ObjectNode node = Json.newObject();
//            node.put("id", ro.getRouteId());
//            node.put("advId", ro.getAdventureId());
//            node.put("startPlaceId", ro.getStartPlaceId());
//            node.put("address", po.getAddress());
//            PlaceAdventurerPreference pref = new PlaceAdventurerPreferenceDAO().get(po.getOptionId(), usr.getId());
//            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
//            node.put("voteCount", Json.toJson(new PlaceAdventurerPreferenceDAO().counts(po.getOptionId())));
//            node.put("voteAdventurers", Json.toJson(new PlaceAdventurerPreferenceDAO().adventurersNames(po.getOptionId())));
//
//            places.add(node);
//        }
//
//        return ok(Json.toJson(places));
//    }

}
