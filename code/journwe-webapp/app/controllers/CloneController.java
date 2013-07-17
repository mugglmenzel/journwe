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

        // Create a new name for the Adventure clone
        String cloneAdvName = filledForm.get("name");

        // Clone ... clone ... clone ...
        Adventure originalAdv = new AdventureDAO().get(originalAdvId);
        Adventure cloneAdv = CloneCommand.initAdventureCloning(cloneAdvName, originalAdv);
        new AdventureDAO().save(cloneAdv);

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        // Always add myself to the Adventure clone
        Adventurer me = CloneCommand.initAdventurerCloning(usr,cloneAdv);
        new AdventurerDAO().save(me);

        CloneCommand clonecmd = new CloneCommand(me,cloneAdv,filledForm);

         if(clonecmd.isCloneAdventurers()) {
        // Add the other adventurers
        for(Adventurer addi : new AdventurerDAO().all(originalAdvId)) {
            // except myself, I'm already in
            if(!addi.getUserId().equals(me.getUserId())) {
                // Make the other guys applicants or something like that.
                // Maybe they don't want to participate.
                Adventurer adventurerClone = (Adventurer)clonecmd.cloneOf(addi);
                new AdventurerDAO().save(adventurerClone);
            }
        }
        }

        if(clonecmd.isClonePlaceOptions()) {
        // Clone place options
        for(PlaceOption po : new PlaceOptionDAO().all(originalAdvId)) {
            PlaceOption placeOptionClone = (PlaceOption)clonecmd.cloneOf(po);
            new PlaceOptionDAO().save(placeOptionClone);
        }
        }

        if(clonecmd.isCloneTimeOptions()) {
        // Clone time options
        for(TimeOption to : new TimeOptionDAO().all(originalAdvId)) {
            TimeOption timeOptionClone = (TimeOption)clonecmd.cloneOf(to);
            new TimeOptionDAO().save(timeOptionClone);
        }
        }

        if(clonecmd.isCloneTodo()) {
            // Clone time options
            for(models.adventure.checklist.Todo todo : new TodoDAO().all(originalAdvId)) {
                models.adventure.checklist.Todo todoClone = (models.adventure.checklist.Todo)clonecmd.cloneOf(todo);
                if(todoClone!=null)
                    new TodoDAO().save(todoClone);
            }
        }

        flash("success", "We are the Borg. Resistance is futile. Your Adventure has been assimilated.");

        return redirect(routes.AdventureController.getIndex(cloneAdv.getId()));
    }

    public static class CloneCommand {
        private boolean cloneAdventurers = false;
        private boolean clonePlaceOptions = false;
        private boolean cloneTimeOptions = false;
        private boolean cloneTodo = false;

        private Adventurer me;
        private Adventure clone;

        protected CloneCommand(Adventurer me, Adventure clone, DynamicForm filledForm) {
            this.me = me;
            this.clone = clone;
            cloneAdventurers = new Boolean(filledForm.get("cloneAdventurers"));
            clonePlaceOptions = new Boolean(filledForm.get("clonePlaceOptions"));
            cloneTimeOptions = new Boolean(filledForm.get("cloneTimeOptions"));
            cloneTodo = new Boolean(filledForm.get("cloneTodo"));
            Logger.debug("Clone adventurers: "+cloneAdventurers);
            Logger.debug("Clone place options: "+clonePlaceOptions);
            Logger.debug("Clone time options: "+cloneTimeOptions);
            Logger.debug("Clone todo: "+cloneTodo);
        }

        public boolean isCloneAdventurers() {
            return cloneAdventurers;
        }

        public boolean isClonePlaceOptions() {
            return clonePlaceOptions;
        }

        public boolean isCloneTimeOptions() {
            return cloneTimeOptions;
        }

        public boolean isCloneTodo() {
            return cloneTodo;
        }

        /**
         * Helper method that initializes the Adventure clone with basic info,
         * such as description, image, etc.
         *
         * @param cloneAdvName The name of the Adventure clone.
         * @param originalAdv The original adventure that the clone is based on.
         * @return
         */
        protected static Adventure initAdventureCloning(final String cloneAdvName, final Adventure originalAdv) {
            Adventure cloneAdv = new Adventure();
            cloneAdv.setName(cloneAdvName);
            cloneAdv.setDescription(originalAdv.getDescription());
            cloneAdv.setImage(originalAdv.getImage());
            cloneAdv.setInspirationId(originalAdv.getInspirationId());
            cloneAdv.setLimit(originalAdv.getLimit());
            cloneAdv.setLimited(originalAdv.isLimited());
            //cloneAdv.setPublish();
            //cloneAdv.setFavoritePlaceId();
            //cloneAdv.setFavoriteTimeId();
            return cloneAdv;
        }

        /**
         * Helper method that connects adds the creator of the Adventure clone
         * as Adventurer.
         *
         * @param usr Creator of the Adventure clone.
         * @param cloneAdv The newly created clone.
         * @return
         */
        protected static Adventurer initAdventurerCloning(final User usr, final Adventure cloneAdv) {
            Adventurer me = new Adventurer();
            me.setAdventureId(cloneAdv.getId());
            me.setUserId(usr.getId());
            me.setParticipationStatus(EAdventurerParticipation.GOING);
            return me;
        }

        /**
         * Helper method for cloning components of an Adventure.
         *
         * @param object The object that is copied into the Adventure clone.
         * @return The Adventure clone with an additional object (e.g. PlaceOption).
         */
        protected Object cloneOf(Object object) {
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
            }
            if(object instanceof models.adventure.checklist.Todo) {
                models.adventure.checklist.Todo original = (models.adventure.checklist.Todo)object;
                models.adventure.checklist.Todo toReturn = new models.adventure.checklist.Todo();
                // If the other adventurers are not copied, only clone my own todo list.
                if(!cloneAdventurers && !original.getUserId().equals(me.getUserId())) {
                    toReturn.setAdventureId(clone.getId());
                    toReturn.setStatus(original.getStatus());
                    toReturn.setTitle(original.getTitle());
                    toReturn.setUserId(original.getUserId());
                    return toReturn;
                }
                return null;
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
}

