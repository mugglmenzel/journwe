package controllers.api.json;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.Adventure;
import models.adventure.EPreferenceVote;
import models.adventure.comment.Comment;
import models.adventure.place.PlaceOption;
import models.adventure.place.PlaceOptionRating;
import models.adventure.place.PlacePreference;
import models.auth.SecuredUser;
import models.authorization.AuthorizationMessage;
import models.authorization.JournweAuthorization;
import models.dao.*;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.CommentDAO;
import models.dao.adventure.PlaceOptionDAO;
import models.dao.adventure.PlacePreferenceDAO;
import models.dao.user.UserDAO;
import models.notifications.helper.AdventurerNotifier;
import models.user.User;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @Security.Authenticated(SecuredUser.class)
    public static Result getPlaces(String advId) {
        if (!new JournweAuthorization(advId).canViewPlaces())
            return AuthorizationMessage.notAuthorizedResponse();

        String favId = new AdventureDAO().get(advId).getFavoritePlaceId();

        List<ObjectNode> places = new ArrayList<ObjectNode>();
        for (PlaceOption po : new PlaceOptionDAO().all(advId)) {
            ObjectNode node = placeToJSON(po);
            node.put("favorite", favId != null && po.getPlaceId().equals(favId));
            places.add(node);
        }

        return ok(Json.toJson(Json.toJson(places)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getFavoritePlace(String advId) {
        if (!new JournweAuthorization(advId).canViewFavoritePlace())
            return AuthorizationMessage.notAuthorizedResponse();
        if (new PlaceOptionDAO().count(advId) < 1) return ok(Json.toJson(""));

        String favId = new AdventureDAO().get(advId).getFavoritePlaceId();
        PlaceOption autoFav = autoFavorite(advId);
        PlaceOption fav = (favId == null) ? autoFav : new PlaceOptionDAO().get(advId, favId);

        ObjectNode node = Json.newObject();
        node.put("favorite", Json.toJson(fav));
        node.put("autoFavorite", Json.toJson(autoFav));

        return ok(node);
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result setFavoritePlace(String advId) {
        if (!new JournweAuthorization(advId).canViewFavoritePlace())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm data = form().bindFromRequest();
        String favId = data.get("favoritePlaceId");
        Adventure adv = new AdventureDAO().get(advId);
        adv.setFavoritePlaceId(favId);
        new AdventureDAO().save(adv);

        if (favId != null && !"".equals(favId)) {
            new AdventurerNotifier().notifyAdventurers(advId, "Favorite Place is now " + new PlaceOptionDAO().get(advId, adv.getFavoritePlaceId()).getAddress() + ".", "Favorite Place");
            return ok(Json.toJson(new PlaceOptionDAO().get(advId, favId)));
        } else {
            return ok(Json.toJson(autoFavorite(advId)));
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result addPlace(String advId) {
        if (!new JournweAuthorization(advId).canEditPlaces())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm requestData = form().bindFromRequest();

        PlaceOption place = new PlaceOption();
        place.setAdventureId(advId);
        place.setAddress(requestData.get("address"));
        try {
            place.setLatitude(new Double(requestData.get("lat")));
            place.setLongitude(new Double(requestData.get("lng")));
        } catch (Exception e) {
            return badRequest();
        }
        new PlaceOptionDAO().save(place);

        User user = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Comment comment = new Comment();
        PlacePreference pref = new PlacePreference();

        if (user != null) {
            if (requestData.get("comment") != null && !"".equals(requestData.get("comment"))) {
                comment.setText(requestData.get("comment"));
                comment.setThreadId(advId + "places");
                comment.setTimestamp(new Long(DateTime.now().getMillis()));
                comment.setUserId(user.getId());
                new CommentDAO().save(comment);
            }

            pref.setPlaceOptionId(place.getOptionId());
            pref.setUserId(user.getId());
            new PlacePreferenceDAO().save(pref);
        }

        ObjectNode node = placeToJSON(place);

        return created(Json.toJson(node));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result voteParam(String advId) {
        return vote(advId, form().bindFromRequest().get("placeId"));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result vote(String advId, String placeId) {
        if (!new JournweAuthorization(advId).canVoteForPlaces())
            return AuthorizationMessage.notAuthorizedResponse();
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        PlaceOption place = new PlaceOptionDAO().get(advId, placeId);
        String vote = form().bindFromRequest().get("vote").toUpperCase();
        Double voteGravity = new Double(form().bindFromRequest().get("voteGravity"));

        PlacePreference pref = new PlacePreferenceDAO().get(place.getOptionId(), usr.getId());
        if (pref == null) {
            pref = new PlacePreference();
            pref.setPlaceOptionId(place.getOptionId());
            pref.setUserId(usr.getId());
        }

        try {
            pref.setVoteGravity(voteGravity != null ? voteGravity : 0.6D);
            pref.setVote(EPreferenceVote.valueOf(vote));
        } catch (IllegalArgumentException e) {
            Logger.error("Got unknown value for vote! value: " + vote);
        }

        new PlacePreferenceDAO().save(pref);

        //PlaceOption place = PlaceOption.fromId(pref.getPlaceOptionId());
        //place = new PlaceOptionDAO().get(place.getAdventureId(), place.getAddress());

        ObjectNode node = placeToJSON(place);

        String favId = new AdventureDAO().get(advId).getFavoritePlaceId();
        node.put("favorite", favId != null && place.getPlaceId().equals(favId));

        return ok(Json.toJson(node));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result deletePlace(String advId, String placeId) {
        if (!new JournweAuthorization(advId).canEditPlaces())
            return AuthorizationMessage.notAuthorizedResponse();
        new PlaceOptionDAO().delete(advId, placeId);
        return ok();
    }


    private static ObjectNode placeToJSON(PlaceOption place) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        PlacePreference pref = new PlacePreferenceDAO().get(place.getOptionId(), usr.getId());

        ObjectNode node = Json.newObject();
        node.put("id", place.getOptionId());
        node.put("advId", place.getAdventureId());
        node.put("placeId", place.getPlaceId());
        node.put("address", place.getAddress());
        node.put("lat", place.getLatitude().doubleValue());
        node.put("lng", place.getLongitude().doubleValue());
        node.put("vote", (pref != null) ? pref.getVote().toString() : EPreferenceVote.MAYBE.toString());
        node.put("voteGravity", (pref != null) ? pref.getVoteGravity() : 0.6D);
        node.put("voteCount", Json.toJson(new PlacePreferenceDAO().counts(place.getOptionId())));
        node.put("voteAdventurers", Json.toJson(new PlacePreferenceDAO().adventurersNames(place.getOptionId())));
        node.put("voteGroup", Json.toJson(getPlaceGroupRating(place).getRating()));

        return node;
    }

    private static PlaceOptionRating getPlaceGroupRating(PlaceOption place) {
        double sum = 0D;
        List<PlacePreference> prefs = new PlacePreferenceDAO().all(place.getOptionId());

        if (prefs.size() == 0)
            return new PlaceOptionRating(place.getPlaceId(), 0D);

        for (PlacePreference pref : prefs)
            if (0D >= pref.getVoteGravity() || EPreferenceVote.NO.equals(pref.getVote()))
                return new PlaceOptionRating(place.getPlaceId(), 0D);
            else
                sum += pref.getVoteGravity();

        return new PlaceOptionRating(place.getPlaceId(), new Double(sum / prefs.size()));
    }


    private static PlaceOption autoFavorite(String advId) {
        List<PlaceOptionRating> ratings = new ArrayList<PlaceOptionRating>();
        List<PlaceOption> placeOptions = new PlaceOptionDAO().all(advId);

        if(placeOptions == null || !(placeOptions.size() > 0)) return null;

        for (PlaceOption po : placeOptions) {
            PlaceOptionRating rating = getPlaceGroupRating(po);
            if (rating != null && 0D < rating.getRating()) ratings.add(rating);
        }
        Logger.debug("List of Ratings: " + ratings);

        String favId = ratings.size() > 0 ? Collections.max(ratings, new Comparator<PlaceOptionRating>() {
            @Override
            public int compare(PlaceOptionRating placeOptionRating, PlaceOptionRating placeOptionRating2) {
                return placeOptionRating.getRating().compareTo(placeOptionRating2.getRating());
            }
        }).getPlaceOptionId() : placeOptions.iterator().next().getOptionId();
        Logger.debug("got autofav with id " + favId);

        return new PlaceOptionDAO().get(advId, favId);
    }
}