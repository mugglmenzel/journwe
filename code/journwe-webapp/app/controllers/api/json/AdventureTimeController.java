package controllers.api.json;

import com.feth.play.module.pa.PlayAuthenticate;
import models.UserManager;
import models.adventure.Adventure;
import models.adventure.EPreferenceVote;
import models.adventure.time.TimeOptionRating;
import models.adventure.time.TimePreference;
import models.adventure.time.TimeOption;
import models.auth.SecuredUser;
import models.authorization.AuthorizationMessage;
import models.authorization.JournweAuthorization;
import models.dao.adventure.*;
import models.dao.user.UserDAO;
import models.notifications.helper.AdventurerNotifier;
import models.user.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.*;

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

        String favId = new AdventureDAO().get(advId).getFavoriteTimeId();

        List<TimeOption> timeOptions = new ArrayList<TimeOption>();
        timeOptions.addAll(new TimeOptionDAO().all(advId));
        java.util.Collections.sort(timeOptions);

        List<ObjectNode> times = new ArrayList<ObjectNode>();

        for (TimeOption to : timeOptions) {
            ObjectNode node = timeToJSON(to);
            node.put("favorite", favId != null && to.getTimeId().equals(favId));
            times.add(node);
        }

        return ok(Json.toJson(times));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getFavoriteTime(String advId) {
        if (!new JournweAuthorization(advId).canViewFavoriteDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();

        if (new TimeOptionDAO().count(advId) < 1) return ok(Json.toJson(""));

        String favId = new AdventureDAO().get(advId).getFavoriteTimeId();
        TimeOption autoFav = autoFavorite(advId);
        TimeOption fav = (favId == null) ? autoFav : new TimeOptionDAO().get(advId, favId);

        ObjectNode node = Json.newObject();
        node.put("favorite", Json.toJson(fav));
        node.put("autoFavorite", Json.toJson(autoFav));

        return ok(node);
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

        if (favId != null && !"".equals(favId)) {
            new AdventurerNotifier().notifyAdventurers(advId, "Favorite Time is now " + new TimeOptionDAO().get(advId, adv.getFavoriteTimeId()).getStartDate() + ".", "Favorite Time");
            return ok(Json.toJson(new TimeOptionDAO().get(advId, favId)));
        } else {
            return ok(Json.toJson(autoFavorite(advId)));
        }
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
            new AdventurerNotifier().notifyAdventurers(advId, "The new time option " + df.format(time.getStartDate()) + (time.getStartDate().equals(time.getEndDate()) ?  "-" + df.format(time.getEndDate()) : "") + " has been added.", "New Time Option");

            Logger.info("returning start date " + time.getStartDate().toString() + ", " + df.format(time.getStartDate()));
            ObjectNode node = timeToJSON(time);

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

    @Security.Authenticated(SecuredUser.class)
    public static Result voteParam(String advId) {
        return vote(advId, form().bindFromRequest().get("timeId"));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result vote(String advId, String timeId) {
        if (!new JournweAuthorization(advId).canVoteForDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();

        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        TimeOption time = new TimeOptionDAO().get(advId, timeId);
        String vote = form().bindFromRequest().get("vote").toUpperCase();
        Double voteGravity = new Double(form().bindFromRequest().get("voteGravity"));

        TimePreference pref = new TimePreferenceDAO().get(time.getOptionId(), usr.getId());
        if (pref == null) {
            pref = new TimePreference();
            pref.setTimeOptionId(time.getOptionId());
            pref.setAdventurerId(usr.getId());
        }

        try {
            if(voteGravity != null) pref.setVoteGravity(voteGravity);
            if(vote !=  null && !"".equals(vote)) pref.setVote(EPreferenceVote.valueOf(vote));
        } catch (IllegalArgumentException e) {
            Logger.error("Got unknown value for vote! value: " + vote);
        }

        new TimePreferenceDAO().save(pref);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        new AdventurerNotifier().notifyAdventurers(advId, usr.getName() + " voted on time option " + df.format(time.getStartDate()) + (time.getStartDate().equals(time.getEndDate()) ?  "-" + df.format(time.getEndDate()) : "") + ".", "Vote Time Option");


        ObjectNode node = timeToJSON(time);

        String favId = new AdventureDAO().get(advId).getFavoriteTimeId();
        node.put("favorite", favId != null && time.getTimeId().equals(favId));

        return ok(Json.toJson(node));
    }

    public static Result deleteTime(String advId, String timeId) {
        if (!new JournweAuthorization(advId).canEditFavoriteDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();
        new TimeOptionDAO().delete(advId, timeId);
        return ok();
    }


    private static ObjectNode timeToJSON(TimeOption time) {
        ObjectNode node = Json.newObject();
        if(time == null) return node;

        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        TimePreference pref = new TimePreferenceDAO().get(time.getOptionId(), usr.getId());

        node.put("id", time.getOptionId());
        node.put("advId", time.getAdventureId());
        node.put("timeId", time.getTimeId());
        node.put("startDate", time.getStartDate() != null ? time.getStartDate().getTime() : new Date().getTime());
        node.put("endDate", time.getEndDate() != null ? time.getEndDate().getTime() : new Date().getTime());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteGravity", (pref != null) ? pref.getVoteGravity() : 0.5D);
        node.put("voteCount", Json.toJson(new TimePreferenceDAO().counts(time.getOptionId())));
        node.put("voteAdventurers", Json.toJson(new TimePreferenceDAO().adventurersNames(time.getOptionId())));
        node.put("voteGroup", Json.toJson(getTimeGroupRating(time).getRating()));

        return node;
    }


    private static TimeOptionRating getTimeGroupRating(TimeOption time) {
        double sum = 0D;
        List<TimePreference> prefs = new TimePreferenceDAO().all(time.getOptionId());

        if (prefs.size() == 0)
            return new TimeOptionRating(time.getTimeId(), 0D);

        for (TimePreference pref : prefs)
            if (0D >= pref.getVoteGravity())
                return new TimeOptionRating(time.getTimeId(), 0D);
            else
                sum += pref.getVoteGravity();

        return new TimeOptionRating(time.getTimeId(), new Double(sum / prefs.size()));
    }

    private static TimeOption autoFavorite(String advId) {
        List<TimeOptionRating> ratings = new ArrayList<TimeOptionRating>();
        List<TimeOption> timeOptions = new TimeOptionDAO().all(advId);

        if(timeOptions == null || timeOptions.size() < 1) return null;

        for (TimeOption po : timeOptions) {
            TimeOptionRating rating = getTimeGroupRating(po);
            if (rating != null && 0D < rating.getRating()) ratings.add(rating);
        }
        Logger.debug("List of Ratings: " + ratings);

        String favId = ratings.size() > 0 ? Collections.max(ratings, new Comparator<TimeOptionRating>() {
            @Override
            public int compare(TimeOptionRating timeOptionRating, TimeOptionRating timeOptionRating2) {
                return timeOptionRating.getRating().compareTo(timeOptionRating2.getRating());
            }
        }).getTimeOptionId() : timeOptions.iterator().next().getOptionId();
        Logger.debug("got time autofav with id " + favId);

        return new TimeOptionDAO().get(advId, favId);
    }

}
