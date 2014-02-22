package models.authorization;

import com.feth.play.module.pa.PlayAuthenticate;
import models.UserManager;
import models.adventure.AdventureAuthorization;
import models.adventure.EAuthorizationRole;
import models.dao.AdventureAuthorizationDAO;
import models.dao.user.UserDAO;
import models.user.User;
import play.Logger;
import play.cache.Cache;
import play.mvc.Http;

import java.util.concurrent.Callable;

public class JournweAuthorization {

    private AbstractAuthorization auth;

    public JournweAuthorization(String advId) {
        this.auth = getAuthorization(advId);
    }

    // ADVENTURE INFO
    public boolean canEditAdventureImage() {
        return (auth != null) ? auth.canEditAdventureImage() : false;
    }

    public boolean canViewAdventureImage() {
        return (auth != null) ? auth.canViewAdventureImage() : false;
    }

    public boolean canEditAdventureTitle() {
        return (auth != null) ? auth.canEditAdventureTitle() : false;
    }

    public boolean canViewAdventureTitle() {
        return (auth != null) ? auth.canViewAdventureTitle() : false;
    }

    public boolean canEditAdventureComments() {
        return (auth != null) ? auth.canEditAdventureComments() : false;
    }

    public boolean canViewAdventureComments() {
        return (auth != null) ? auth.canViewAdventureComments() : false;
    }

    public boolean canEditAdventureDescription() {
        return (auth != null) ? auth.canEditAdventureDescription() : false;
    }

    public boolean canViewAdventureDescription() {
        return (auth != null) ? auth.canViewAdventureDescription() : false;
    }

    // PLACE
    public boolean canViewAdventurePlacesWidget() {
        return (auth != null) ? auth.canViewAdventurePlacesWidget() : false;
    }

    public boolean canEditPlaces() {
        return (auth != null) ? auth.canEditPlaces() : false;
    }

    public boolean canViewPlaces() {
        return (auth != null) ? auth.canViewPlaces() : false;
    }

    public boolean canVoteForPlaces() {
        return (auth != null) ? auth.canVoteForPlaces() : false;
    }

    public boolean canChangeVoteOnOffForPlaces() {
        return (auth != null) ? auth.canChangeVoteOnOffForPlaces() : false;
    }

    public boolean canViewFavoritePlace() {
        return (auth != null) ? auth.canViewFavoritePlace() : false;
    }

    public boolean canEditPlaceComments() {
        return (auth != null) ? auth.canEditPlaceComments() : false;
    }

    public boolean canViewPlaceComments() {
        return (auth != null) ? auth.canViewPlaceComments() : false;
    }

    public boolean canViewVotesForPlaces() {
        return (auth != null) ? auth.canViewVotesForPlaces() : false;
    }

    // ADVENTURERS
    public boolean canViewAdventureAdventurersWidget() {
        return (auth != null) ? auth.canViewAdventureAdventurersWidget() : false;
    }

    public boolean canViewAdventurerParticipants() {
        return (auth != null) ? auth.canViewAdventurerParticipants() : false;
    }

    public boolean canEditAdventurerParticipationStatus() {
        return (auth != null) ? auth.canEditAdventurerParticipationStatus() : false;
    }

    public boolean canViewAdventurerParticipationStatus() {
        return (auth != null) ? auth.canViewAdventurerParticipationStatus() : false;
    }

    public boolean canInviteAdventurerParticipants() {
        return (auth != null) ? auth.canInviteAdventurerParticipants() : false;
    }

    public boolean canAcceptAdventurerApplicants() {
        return (auth != null) ? auth.canAcceptAdventurerApplicants() : false;
    }

    public boolean canRemoveAdventurerParticipants() {
        return (auth != null) ? auth.canRemoveAdventurerParticipants() : false;
    }

    public boolean canEditAdventurerComments() {
        return (auth != null) ? auth.canEditAdventurerComments() : false;
    }

    public boolean canViewAdventurerComments() {
        return (auth != null) ? auth.canViewAdventurerComments() : false;
    }

    // TIME
    public boolean canViewAdventureTimeWidget() {
        return (auth != null) ? auth.canViewAdventureTimeWidget() : false;
    }

    public boolean canEditDateAndTime() {
        return (auth != null) ? auth.canEditDateAndTime() : false;
    }

    public boolean canViewDateAndTime() {
        return (auth != null) ? auth.canViewDateAndTime() : false;
    }

    public boolean canChangeVoteOnOffForDateAndTime() {
        return (auth != null) ? auth.canChangeVoteOnOffForDateAndTime() : false;
    }

    public boolean canVoteForDateAndTime() {
        return (auth != null) ? auth.canVoteForDateAndTime() : false;
    }

    public boolean canViewVotesForDateAndTime() {
        return (auth != null) ? auth.canViewVotesForDateAndTime() : false;
    }

    public boolean canViewFavoriteDateAndTime() {
        return (auth != null) ? auth.canViewFavoriteDateAndTime() : false;
    }

    public boolean canEditFavoriteDateAndTime() {
        return (auth != null) ? auth.canEditFavoriteDateAndTime() : false;
    }

    public boolean canEditDateAndTimeComments() {
        return (auth != null) ? auth.canEditDateAndTimeComments() : false;
    }

    public boolean canViewDateAndTimeComments() {
        return (auth != null) ? auth.canViewDateAndTimeComments() : false;
    }

    // TODOLIST
    public boolean canViewAdventureTodoWidget() {
        return (auth != null) ? auth.canViewAdventureTodoWidget() : false;
    }

    public boolean canEditTodoItem() {
        return (auth != null) ? auth.canEditTodoItem() : false;
    }

    public boolean canViewTodoItem() {
        return (auth != null) ? auth.canViewTodoItem() : false;
    }

    public boolean canSetTodoListToCompleted() {
        return (auth != null) ? auth.canSetTodoListToCompleted() : false;
    }

    public boolean canEditTodoComments() {
        return (auth != null) ? auth.canEditTodoComments() : false;
    }

    public boolean canViewTodoComments() {
        return (auth != null) ? auth.canViewTodoComments() : false;
    }

    // FILE
    public boolean canViewAdventureFilesWidget() {
        return (auth != null) ? auth.canViewAdventureFilesWidget() : false;
    }

    public boolean canUploadFiles() {
        return (auth != null) ? auth.canUploadFiles() : false;
    }

    public boolean canViewAndDownloadFiles() {
        return (auth != null) ? auth.canViewAndDownloadFiles() : false;
    }

    public boolean canDeleteFiles() {
        return (auth != null) ? auth.canDeleteFiles() : false;
    }

    public boolean canEditFilesComments() {
        return (auth != null) ? auth.canEditFilesComments() : false;
    }

    public boolean canViewFilesComments() {
        return (auth != null) ? auth.canViewFilesComments() : false;
    }

    // DISCUSSION
    public boolean canViewAdventureDiscussionWidget() {
        return (auth != null) ? auth.canViewAdventureDiscussionWidget() : false;
    }

    // OFFERS
    public boolean canViewAdventureOffersWidget() {
        return (auth != null) ? auth.canViewAdventureOffersWidget() : false;
    }

    public boolean canViewOfferComments() {
        return (auth != null) ? auth.canViewOfferComments() : false;
    }

    /**
     * Helper
     *
     * @param advId
     * @return
     */
    private AbstractAuthorization getAuthorization(final String advId) {
        try {
            final User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
            if (usr == null)
                return null;

            // Load JournweAuthorization classname from database
            AdventureAuthorization model = null;
            try {
                model = Cache.getOrElse("adventureAuthorization." + advId + "." + usr.getId(), new Callable<AdventureAuthorization>() {
                    @Override
                    public AdventureAuthorization call() throws Exception {
                        Logger.debug("fetched authorization from dynamodb");
                        return new AdventureAuthorizationDAO().get(advId, usr.getId());
                    }
                }, 24 * 3600);
            } catch (Exception e) {
                Logger.error("Couldn't get Authorization for adv " + advId + ", usr " + usr.getId() + " from db", e);
                model = new AdventureAuthorizationDAO().get(advId, usr.getId());
            }


            if (model == null) return null;
            EAuthorizationRole authorizationRole = model.getAuthorizationRole();
            String className = authorizationRole.getClassName();
            Class clazz = Class.forName(className);
            Object obj = clazz.newInstance();
            if (obj instanceof AbstractAuthorization) {
                AbstractAuthorization authorization = (AbstractAuthorization) obj;
                return authorization;
            }

        } catch (ClassNotFoundException e) {
            Logger.error("Auth class not found", e);
        } catch (InstantiationException e) {
            Logger.error("Auth class could not be instantiated", e);
        } catch (IllegalAccessException e) {
            Logger.error("Auth class illegal access", e);
        }

        return null;
    }

}
