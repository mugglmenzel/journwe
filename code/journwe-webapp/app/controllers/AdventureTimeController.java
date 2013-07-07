package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import controllers.auth.SecuredAdminUser;
import models.Inspiration;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeAdventurerPreference;
import models.adventure.time.TimeOption;
import models.dao.*;
import models.user.User;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.06.13
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
public class AdventureTimeController extends Controller {

    public static Form<TimeOption> timeForm = form(TimeOption.class);

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getTimes(String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        List<ObjectNode> times = new ArrayList<ObjectNode>();
        for (TimeOption to : new TimeOptionDAO().all(advId)) {
            ObjectNode node = Json.newObject();
            node.put("id", to.getId());
            node.put("startDate", to.getStartDate() != null  ? new SimpleDateFormat().format(to.getStartDate()) : "");
            node.put("endDate", to.getEndDate() != null ? new SimpleDateFormat().format(to.getEndDate()): "");
            TimeAdventurerPreference pref = new TimeAdventurerPreferenceDAO().get(to.getId(), usr.getId());
            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
            node.put("voteCount", Json.toJson(new TimeAdventurerPreferenceDAO().counts(to.getId())));

            times.add(node);
        }

        return ok(Json.toJson(times));
    }

    /*
    public static Result getTime(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        Inspiration ins = new InspirationDAO().get(adv.getInspirationId());
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        return ok(getTime.render(adv, ins, advr, timeForm));
    }
    */

    public static Result addTime(String advId) {
        TimeOption opt = form(TimeOption.class).bindFromRequest().get();
        opt.setAdventureId(advId);
        new TimeOptionDAO().save(opt);
        return ok(Json.toJson(opt));
    }

    public static Result vote(String optId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        String vote = form().bindFromRequest().get("vote").toUpperCase();

        TimeAdventurerPreference pref = new TimeAdventurerPreferenceDAO().get(optId, usr.getId());
        if (pref == null) {
            pref = new TimeAdventurerPreference();
            pref.setTimeOptionId(optId);
            pref.setAdventurerId(usr.getId());
        }

        try {
            pref.setVote(EPreferenceVote.valueOf(vote));
        } catch (IllegalArgumentException e) {
            Logger.error("Got unknown value for vote! value: " + vote);
        }

        new TimeAdventurerPreferenceDAO().save(pref);

        return ok(Json.toJson(pref));
    }


}
