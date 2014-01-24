package controllers.api.json;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.Adventure;
import models.adventure.EPreferenceVote;
import models.adventure.time.TimePreference;
import models.adventure.time.TimeOption;
import models.auth.SecuredUser;
import models.authorization.AuthorizationMessage;
import models.authorization.JournweAuthorization;
import models.dao.adventure.TimePreferenceDAO;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.TimeOptionDAO;
import models.dao.user.UserDAO;
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

    @Security.Authenticated(SecuredUser.class)
    public static Result getTimes(String advId) {
        if (!new JournweAuthorization(advId).canViewDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        String favId = new AdventureDAO().get(advId).getFavoriteTimeId();

        List<ObjectNode> times = new ArrayList<ObjectNode>();
        List<TimeOption> timeOptions = new ArrayList<TimeOption>();
        timeOptions.addAll(new TimeOptionDAO().all(advId));
        java.util.Collections.sort(timeOptions);
        
        for (TimeOption to : timeOptions) {
            ObjectNode node = Json.newObject();
            node.put("id", to.getOptionId());
            node.put("advId", to.getAdventureId());
            node.put("timeId", to.getTimeId());
            node.put("startDate", to.getStartDate() != null ? to.getStartDate().getTime() : new Date().getTime());
            node.put("endDate", to.getEndDate() != null ? to.getEndDate().getTime() : new Date().getTime());
            TimePreference pref = new TimePreferenceDAO().get(to.getOptionId(), usr.getId());
            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
            node.put("voteCount", Json.toJson(new TimePreferenceDAO().counts(to.getOptionId())));
            node.put("voteAdventurers", Json.toJson(new TimePreferenceDAO().adventurersNames(to.getOptionId())));
            if (favId != null && to.getTimeId().equals(favId)){
                node.put("favorite", true);
            }
            times.add(node);
        }

        ObjectNode result = Json.newObject();
        result.put("times", Json.toJson(times));
        result.put("favoriteTime", favId != null ? Json.toJson(new TimeOptionDAO().get(advId, favId)) : Json.toJson(""));


        return ok(Json.toJson(result));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getFavoriteTime(String advId) {
        if (!new JournweAuthorization(advId).canViewFavoriteDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();

        if (new TimeOptionDAO().count(advId) < 1) return ok(Json.toJson(""));

        String favId = new AdventureDAO().get(advId).getFavoriteTimeId();

        return favId != null ? ok(Json.toJson(new TimeOptionDAO().get(advId, favId))) : ok(Json.toJson(""));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result setFavoriteTime(String advId) {
        if (!new JournweAuthorization(advId).canEditFavoriteDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm data = form().bindFromRequest();
        String favId = data.get("favoriteTimeId");
        Adventure adv = new AdventureDAO().get(advId);
        adv.setFavoriteTimeId(favId);
        new AdventureDAO().save(adv);

        return ok(Json.toJson(new TimeOptionDAO().get(advId, favId)));
    }

    public static Result addTime(String advId) {
        if (!new JournweAuthorization(advId).canEditDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm requestData = form().bindFromRequest();
        Logger.info(requestData.data().toString());

        // DateFormat depends on Locale
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        try {
            TimeOption time = new TimeOption();
            time.setAdventureId(advId);
            time.setStartDate(df.parse(requestData.get("startDate")));
            time.setEndDate(df.parse(requestData.get("endDate")));
            new TimeOptionDAO().save(time);

            User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

            TimePreference pref = new TimePreference();
            pref.setTimeOptionId(time.getOptionId());
            pref.setAdventurerId(usr.getId());
            new TimePreferenceDAO().save(pref);

            Logger.info("returning start date " + time.getStartDate().toString() + ", " + df.format(time.getStartDate()));
            ObjectNode node = Json.newObject();
            node.put("id", time.getOptionId());
            node.put("advId", time.getAdventureId());
            node.put("timeId", time.getTimeId());
            node.put("startDate", time.getStartDate() != null ? time.getStartDate().getTime() : new Date().getTime());
            node.put("endDate", time.getEndDate() != null ? time.getEndDate().getTime() : new Date().getTime());
            node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
            node.put("voteCount", Json.toJson(new TimePreferenceDAO().counts(time.getOptionId())));
            node.put("voteAdventurers", Json.toJson(new TimePreferenceDAO().adventurersNames(time.getOptionId())));

            // Get sorted index
            List<TimeOption> timeOptions = new ArrayList<TimeOption>();
            timeOptions.addAll(new TimeOptionDAO().all(advId));
            java.util.Collections.sort(timeOptions);
            node.put("index", timeOptions.indexOf(time)); 

            return created(Json.toJson(node));
        } catch (ParseException pe) {
            Logger.error("Got a parse exception for date parsing: " + pe.getLocalizedMessage());
            return badRequest();
        }
    }

    public static Result vote(String advId, String timeId) {
        if (!new JournweAuthorization(advId).canVoteForDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        TimeOption time = new TimeOptionDAO().get(advId, timeId);
        String vote = form().bindFromRequest().get("vote").toUpperCase();

        TimePreference pref = new TimePreferenceDAO().get(time.getOptionId(), usr.getId());
        if (pref == null) {
            pref = new TimePreference();
            pref.setTimeOptionId(time.getOptionId());
            pref.setAdventurerId(usr.getId());
        }

        try {
            pref.setVote(EPreferenceVote.valueOf(vote));
        } catch (IllegalArgumentException e) {
            Logger.error("Got unknown value for vote! value: " + vote);
        }

        new TimePreferenceDAO().save(pref);

        ObjectNode node = Json.newObject();
        node.put("id", time.getOptionId());
        node.put("advId", time.getAdventureId());
        node.put("timeId", time.getTimeId());
        node.put("startDate", time.getStartDate() != null ? time.getStartDate().getTime() : new Date().getTime());
        node.put("endDate", time.getEndDate() != null ? time.getEndDate().getTime() : new Date().getTime());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteCount", Json.toJson(new TimePreferenceDAO().counts(time.getOptionId())));
        node.put("voteAdventurers", Json.toJson(new TimePreferenceDAO().adventurersNames(time.getOptionId())));

        String favId = new AdventureDAO().get(advId).getFavoriteTimeId();
        if (favId != null && time.getTimeId().equals(favId)){
            node.put("favorite", true);
        }

        return ok(Json.toJson(node));
    }

    public static Result deleteTime(String advId, String timeId) {
        if (!new JournweAuthorization(advId).canEditFavoriteDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();

        new TimeOptionDAO().delete(advId, timeId);
        return ok();
    }

}