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
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
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
    public static Result getAdventurersOfGroup(String advId, String groupId) {
        List<ObjectNode> adventurersNodes = new ArrayList<ObjectNode>();
        List<Adventurer> adventurers = new AdventurerGroupDAO().getAdventurersOfGroup(advId,groupId);
        for (Adventurer adventurer : adventurers) {
            ObjectNode node = Json.newObject();
            String userId = adventurer.getUserId();
//            node.put("userId", userId);
            String userName = new UserDAO().get(userId).getName();
//            Logger.debug("userName: "+userName);
            node.put("userName", userName);
            adventurersNodes.add(node);
        }
        return ok(Json.toJson(adventurersNodes));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result createGroup(String advId) {
        AdventurerGroup group = createEmptyGroup(advId);
        new AdventurerGroupDAO().save(group);

        return ok("New group created!");
    }

//    @Security.Authenticated(SecuredBetaUser.class)
//    public static Result addGroup(String advId) {
//
//        DynamicForm requestData = form().bindFromRequest();
//
//        AdventurerGroup group = new AdventurerGroup();
//        group.setAdventureId(advId);
//        group.setGroupName(requestData.get("groupName"));
//        group.setGroupDescription(requestData.get("groupDescription"));
//        group.setRouteIds(Arrays.asList(requestData.get("routeIds").split(":")));
//
//        new AdventurerGroupDAO().save(group);
//
//        ObjectNode node = Json.newObject();
//        node.put("groupId", group.getGroupId());
//        node.put("advId", group.getAdventureId());
//        node.put();
//
//
//        return ok(Json.toJson(node));
//    }

    protected static AdventurerGroup createEmptyGroup(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        // Create and save an empty route
        RouteOption route = new RouteOption();
        route.setAdventureId(advId);
        new RouteOptionDAO().save(route);

        AdventurerGroup group = new AdventurerGroup();
        group.setAdventureId(advId);
        group.addAdventurerId(advr.getUserId());
        group.addRouteId(route.getRouteId());

        return group;
    }

}
