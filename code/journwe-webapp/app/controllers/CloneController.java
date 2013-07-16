package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.rosaloves.bitlyj.Jmp;
import com.typesafe.config.ConfigFactory;
import models.adventure.Adventure;
import models.adventure.AdventureShortname;
import models.adventure.Adventurer;
import models.adventure.EAdventurerParticipation;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.auth.SecuredBetaUser;
import models.dao.*;
import models.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.rosaloves.bitlyj.Bitly.shorten;
import static play.data.Form.form;

public class CloneController extends Controller {

    private static DynamicForm cloneAdvForm = form();

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result cloneAndSaveAdventure(final String originalAdvId) {
        // The Dynamic Form has the checkboxes
        // indicating which components of the
        // adventure to cloneAdventure.
        DynamicForm filledForm = cloneAdvForm.bindFromRequest();
        boolean cloneAdventurers = new Boolean(filledForm.get("cloneAdventurers"));
        boolean clonePlaceOptions = new Boolean(filledForm.get("clonePlaceOptions"));
        boolean cloneTimeOptions = new Boolean(filledForm.get("cloneTimeOptions"));
        Logger.debug("Clone adventurers: "+cloneAdventurers);
        Logger.debug("Clone place options: "+clonePlaceOptions);
        Logger.debug("Clone time options: "+cloneTimeOptions);
        // create a new name and shortname
        String cloneAdvName = filledForm.get("name");
        String shortname = filledForm.get("shortname");

        // Clone ... clone ... clone ...
        Adventure originalAdv = new AdventureDAO().get(originalAdvId);
        Adventure cloneAdv = new Adventure();
        cloneAdv.setName(cloneAdvName);
        // TODO should check if this is OK
        cloneAdv.setDescription(originalAdv.getDescription());
        cloneAdv.setImage(originalAdv.getImage());
        cloneAdv.setInspirationId(originalAdv.getInspirationId());
        cloneAdv.setLimit(originalAdv.getLimit());
        cloneAdv.setLimited(originalAdv.isLimited());
        //cloneAdv.setPublish();
        //cloneAdv.setFavoritePlaceId();
        //cloneAdv.setFavoriteTimeId();
        new AdventureDAO().save(cloneAdv);

        String shortURL = routes.AdventureController.getIndex(cloneAdv.getId()).absoluteURL(request());
        if (shortname != null) {
            AdventureShortname advShortname = new AdventureShortname(shortname, cloneAdv.getId());
            new AdventureShortnameDAO().save(advShortname);
            shortURL = routes.AdventureController.getIndexShortname(advShortname.getShortname()).absoluteURL(request());
        }


        try {
            shortURL = request().host().contains("localhost") ?
                    shortURL :
                    Jmp.as(ConfigFactory.load().getString("bitly.username"), ConfigFactory.load().getString("bitly.apiKey")).call(shorten(shortURL)).getShortUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        // Add myself to the cloneAdventure adventure
        Adventurer me = new Adventurer();
        me.setAdventureId(cloneAdv.getId());
        me.setUserId(usr.getId());
        me.setParticipationStatus(EAdventurerParticipation.GOING);
        new AdventurerDAO().save(me);

        if(cloneAdventurers) {
        // Add the other adventurers
        for(Adventurer addi : new AdventurerDAO().all(originalAdvId)) {
            // except myself, I'm already in
            if(!addi.getUserId().equals(me.getUserId())) {
                // Make the other guys applicants or something like that.
                // Maybe they don't want to participate.

                addi.setAdventureId(cloneAdv.getId());
                new AdventurerDAO().save((Adventurer)cloneOf(cloneAdv,addi));
            }
        }
        }

        if(clonePlaceOptions) {
        // Clone place options
        for(PlaceOption po : new PlaceOptionDAO().all(originalAdvId)) {
            po.setAdventureId(cloneAdv.getId());
            new PlaceOptionDAO().save((PlaceOption)cloneOf(cloneAdv,po));
        }
        }

        if(cloneTimeOptions) {
        // Clone time options
        for(TimeOption to : new TimeOptionDAO().all(originalAdvId)) {
            to.setAdventureId(cloneAdv.getId());
            new TimeOptionDAO().save((TimeOption)cloneOf(cloneAdv,to));
        }
        }

        flash("success", "We are the Borg. Resistance is futile. Your Adventure has been assimilated.");

        return redirect(routes.AdventureController.getIndex(cloneAdv.getId()));
    }

    private static Object cloneOf(Adventure clone, Object object) {
        if(object instanceof Adventurer) {
            Adventurer original = (Adventurer)object;
            Adventurer toReturn = new Adventurer();
            toReturn.setAdventureId(clone.getId());
            toReturn.setParticipationStatus(EAdventurerParticipation.APPLICANT);
            toReturn.setUserId(original.getUserId());
            return toReturn;
        }
        if(object instanceof PlaceOption) {
            PlaceOption original = (PlaceOption)object;
            PlaceOption toReturn = new PlaceOption();
            toReturn.setAdventureId(clone.getId());
            toReturn.setDescription(original.getDescription());
            toReturn.setGoogleMapsAddress(original.getGoogleMapsAddress());
            toReturn.setName(original.getName());
            return toReturn;
        }
        if(object instanceof TimeOption) {
            TimeOption original = (TimeOption)object;
            TimeOption toReturn = new TimeOption();
            toReturn.setAdventureId(clone.getId());
            toReturn.setDescription(original.getDescription());
            toReturn.setEndDate(original.getEndDate());
            toReturn.setName(original.getName());
            toReturn.setStartDate(original.getStartDate());
            return toReturn;
        } else {
        try {
            throw new Exception("cloneOf Method failed because the object type did not match");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        }
    }
}

