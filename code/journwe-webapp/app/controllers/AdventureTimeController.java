package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import controllers.auth.SecuredAdminUser;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.time.TimeAdventurerPreference;
import models.adventure.time.TimeOption;
import models.dao.PlaceAdventurerPreferenceDAO;
import models.dao.TimeAdventurerPreferenceDAO;
import models.dao.TimeOptionDAO;
import models.dao.UserDAO;
import models.user.User;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            node.put("name", to.getName());
            node.put("startDate", to.getStartDate() != null ? to.getStartDate().getTime() : new Date().getTime());
            node.put("endDate", to.getEndDate() != null ? to.getEndDate().getTime() : new Date().getTime());
            TimeAdventurerPreference pref = new TimeAdventurerPreferenceDAO().get(to.getId(), usr.getId());
            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
            node.put("voteCount", Json.toJson(new TimeAdventurerPreferenceDAO().counts(to.getId())));
            node.put("voteAdventurers", Json.toJson(new TimeAdventurerPreferenceDAO().adventurersNames(to.getId())));

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

        DynamicForm requestData = form().bindFromRequest();
        Logger.info(requestData.data().toString());

        // DateFormat depends on Locale
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);

        try {
            TimeOption time = new TimeOption();
            time.setAdventureId(advId);
            time.setStartDate(df.parse(requestData.get("startDate")));
            time.setEndDate(df.parse(requestData.get("endDate")));
            time.setName(requestData.get("name"));
            new TimeOptionDAO().save(time);

            User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

            TimeAdventurerPreference pref = new TimeAdventurerPreference();
            pref.setTimeOptionId(time.getId());
            pref.setAdventurerId(usr.getId());
            new TimeAdventurerPreferenceDAO().save(pref);


            ObjectNode node = Json.newObject();
            node.put("id", time.getId());
            node.put("name", time.getName());
            node.put("startDate", time.getStartDate() != null ? time.getStartDate().getTime() : new Date().getTime());
            node.put("endDate", time.getEndDate() != null ? time.getEndDate().getTime() : new Date().getTime());
            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
            node.put("voteCount", Json.toJson(new TimeAdventurerPreferenceDAO().counts(time.getId())));
            node.put("voteAdventurers", Json.toJson(new TimeAdventurerPreferenceDAO().adventurersNames(time.getId())));

            return ok(Json.toJson(node));
        } catch (ParseException pe) {
            Logger.error("Got a parse exception for date parsing: " + pe.getLocalizedMessage());
            return badRequest();
        }
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

        TimeOption time = new TimeOptionDAO().get(pref.getTimeOptionId());

        ObjectNode node = Json.newObject();
        node.put("id", time.getId());
        node.put("name", time.getName());
        node.put("startDate", time.getStartDate() != null ? time.getStartDate().getTime() : new Date().getTime());
        node.put("endDate", time.getEndDate() != null ? time.getEndDate().getTime() : new Date().getTime());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteCount", Json.toJson(new TimeAdventurerPreferenceDAO().counts(time.getId())));
        node.put("voteAdventurers", Json.toJson(new TimeAdventurerPreferenceDAO().adventurersNames(time.getId())));

        return ok(Json.toJson(node));
    }

    public static Result deleteTime(String optId) {
        new TimeOptionDAO().delete(optId);
        return ok();
    }

}
