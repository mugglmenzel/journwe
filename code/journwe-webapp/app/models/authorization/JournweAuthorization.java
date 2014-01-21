package models.authorization;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.AdventureAuthorization;
import models.adventure.EAuthorizationRole;
import models.dao.AdventureAuthorizationDAO;
import models.dao.user.UserDAO;
import models.user.User;
import play.mvc.Http;

public class JournweAuthorization {

    // ADVENTURE INFO
    public static boolean canEditAdventureImage(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditAdventureImage() : false;
    }
    public static boolean canViewAdventureImage(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureImage() : false;
    }
    public static boolean canEditAdventureTitle(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditAdventureTitle() : false;
    }
    public static boolean canViewAdventureTitle(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureTitle() : false;
    }
    public static boolean canEditAdventureComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditAdventureComments() : false;
    }
    public static boolean canViewAdventureComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureComments() : false;
    }
    public static boolean canEditAdventureDescription(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditAdventureDescription() : false;
    }
    public static boolean canViewAdventureDescription(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureDescription() : false;
    }

    // PLACE
    public static boolean canViewAdventurePlacesWidget(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventurePlacesWidget() : false;
    }
    public static boolean canEditPlaces(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditPlaces() : false;
    }
    public static boolean canViewPlaces(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewPlaces() : false;
    }
    public static boolean canVoteForPlaces(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canVoteForPlaces() : false;
    }
    public static boolean canChangeVoteOnOffForPlaces(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canChangeVoteOnOffForPlaces() : false;
    }
    public static boolean canViewFavoritePlace(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewFavoritePlace() : false;
    }
    public static boolean canEditPlaceComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditPlaceComments() : false;
    }
    public static boolean canViewPlaceComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewPlaceComments() : false;
    }
    public static boolean canViewVotesForPlaces(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewVotesForPlaces() : false;
    }

    // ADVENTURERS
    public static boolean canViewAdventureAdventurersWidget(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureAdventurersWidget() : false;
    }
    public static boolean canViewAdventurerParticipants(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventurerParticipants() : false;
    }
    public static boolean canEditAdventurerParticipationStatus(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditAdventurerParticipationStatus() : false;
    }
    public static boolean canViewAdventurerParticipationStatus(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventurerParticipationStatus() : false;
    }
    public static boolean canInviteAdventurerParticipants(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canInviteAdventurerParticipants() : false;
    }
    public static boolean canAcceptAdventurerApplicants(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canAcceptAdventurerApplicants() : false;
    }
    public static boolean canRemoveAdventurerParticipants(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canRemoveAdventurerParticipants() : false;
    }
    public static boolean canEditAdventurerComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditAdventurerComments() : false;
    }
    public static boolean canViewAdventurerComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventurerComments() : false;
    }

    // TIME
    public static boolean canViewAdventureTimeWidget(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureTimeWidget() : false;
    }
    public static boolean canEditDateAndTime(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditDateAndTime() : false;
    }
    public static boolean canViewDateAndTime(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewDateAndTime() : false;
    }
    public static boolean canChangeVoteOnOffForDateAndTime(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canChangeVoteOnOffForDateAndTime() : false;
    }
    public static boolean canVoteForDateAndTime(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canVoteForDateAndTime() : false;
    }
    public static boolean canViewVotesForDateAndTime(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewVotesForDateAndTime() : false;
    }
    public static boolean canViewFavoriteDateAndTime(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewFavoriteDateAndTime() : false;
    }
    public static boolean canEditFavoriteDateAndTime(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditFavoriteDateAndTime() : false;
    }
    public static boolean canEditDateAndTimeComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditDateAndTimeComments() : false;
    }
    public static boolean canViewDateAndTimeComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewDateAndTimeComments() : false;
    }

    // TODOLIST
    public static boolean canViewAdventureTodoWidget(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureTodoWidget() : false;
    }
    public static boolean canEditTodoItem(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditTodoItem() : false;
    }
    public static boolean canViewTodoItem(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewTodoItem() : false;
    }
    public static boolean canSetTodoListToCompleted(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canSetTodoListToCompleted() : false;
    }
    public static boolean canEditTodoComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditTodoComments() : false;
    }
    public static boolean canViewTodoComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewTodoComments() : false;
    }

    // FILE
    public static boolean canViewAdventureFilesWidget(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureFilesWidget() : false;
    }
    public static boolean canUploadFiles(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canUploadFiles() : false;
    }
    public static boolean canViewAndDownloadFiles(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAndDownloadFiles() : false;
    }
    public static boolean canDeleteFiles(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canDeleteFiles() : false;
    }
    public static boolean canEditFilesComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canEditFilesComments() : false;
    }
    public static boolean canViewFilesComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewFilesComments() : false;
    }

    // DISCUSSION
    public static boolean canViewAdventureDiscussionWidget(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureDiscussionWidget() : false;
    }

    // OFFERS
    public static boolean canViewAdventureOffersWidget(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewAdventureOffersWidget() : false;
    }
    public static boolean canViewOfferComments(final String advId) {
        AbstractAuthorization authorization = getAuthorization(advId);
        return (authorization!=null) ? authorization.canViewOfferComments() : false;
    }

    /**
     * Helper
     *
     * @param advId
     * @return
     */
    private static AbstractAuthorization getAuthorization(final String advId) {
        try {
            User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
            if(usr==null)
                return null;
            // Load JournweAuthorization classname from database
            AdventureAuthorization model = new AdventureAuthorizationDAO().get(advId, usr.getId());
            if(model==null) return null;
            EAuthorizationRole authorizationRole = model.getAuthorizationRole();
            String className = authorizationRole.getClassName();
            Class clazz = Class.forName(className);
            Object obj = clazz.newInstance();
            if(obj instanceof AbstractAuthorization) {
                AbstractAuthorization authorization =  (AbstractAuthorization)obj;
                return authorization;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

}
