package models.authorization;

public class AdventureExpertAuthorization extends AbstractAuthorization {

    // ADVENTURE

    @Override
    public boolean canEditAdventureImage() {
        return false;
    }

    @Override
    public boolean canViewAdventureImage() {
        return true;
    }

    @Override
    public boolean canEditAdventureTitle() {
        return false;
    }

    @Override
    public boolean canViewAdventureTitle() {
        return true;
    }

    @Override
    public boolean canEditAdventureComments() {
        return false;
    }

    @Override
    public boolean canViewAdventureComments() {
        return true;
    }

    @Override
    public boolean canEditAdventureDescription() {
        return false;
    }

    @Override
    public boolean canViewAdventureDescription() {
        return true;
    }

    // PLACE
    @Override
    boolean canViewAdventurePlacesWidget() {
        return true;
    }

    @Override
    boolean canEditPlaces() {
        return false;
    }

    @Override
    boolean canViewPlaces() {
        return true;
    }

    @Override
    boolean canVoteForPlaces() {
        return false;
    }

    @Override
    boolean canChangeVoteOnOffForPlaces() {
        return false;
    }

    @Override
    boolean canViewVotesForPlaces() {
        return false;
    }

    @Override
    boolean canViewFavoritePlace() {
        return true;
    }

    @Override
    boolean canEditPlaceComments() {
        return true;
    }

    @Override
    boolean canViewPlaceComments() {
        return true;
    }

    // ADVENTURERS
    @Override
    boolean canViewAdventureAdventurersWidget() {
        return true;
    }

    @Override
    boolean canViewAdventurerParticipants() {
        return true;
    }

    @Override
    boolean canEditAdventurerParticipationStatus() {
        return false;
    }

    @Override
    boolean canViewAdventurerParticipationStatus() {
        return false;
    }

    @Override
    boolean canInviteAdventurerParticipants() {
        return false;
    }

    @Override
    boolean canAcceptAdventurerApplicants() {
        return false;
    }

    @Override
    boolean canRemoveAdventurerParticipants() {
        return false;
    }

    @Override
    boolean canEditAdventurerComments() {
        return true;
    }

    @Override
    boolean canViewAdventurerComments() {
        return true;
    }

    // TIME

    @Override
    boolean canViewAdventureTimeWidget() {
        return true;
    }

    @Override
    boolean canEditDateAndTime() {
        return false;
    }

    @Override
    boolean canViewDateAndTime() {
        return true;
    }

    @Override
    boolean canVoteForDateAndTime() {
        return false;
    }

    @Override
    boolean canChangeVoteOnOffForDateAndTime() {
        return false;
    }

    @Override
    boolean canViewVotesForDateAndTime() {
        return true;
    }

    @Override
    boolean canViewFavoriteDateAndTime() {
        return true;
    }

    @Override
    boolean canEditFavoriteDateAndTime() {
        return false;
    }

    @Override
    boolean canEditDateAndTimeComments() {
        return true;
    }

    @Override
    boolean canViewDateAndTimeComments() {
        return true;
    }

    // TODOLIST

    @Override
    boolean canViewAdventureTodoWidget() {
        return true;
    }

    @Override
    boolean canEditTodoItem() {
        return false;
    }

    @Override
    boolean canViewTodoItem() {
        return true;
    }

    @Override
    boolean canSetTodoListToCompleted() {
        return false;
    }

    @Override
    boolean canEditTodoComments() {
        return true;
    }

    @Override
    boolean canViewTodoComments() {
        return true;
    }

    // FILES

    @Override
    boolean canViewAdventureFilesWidget() {
        return true;
    }

    @Override
    boolean canUploadFiles() {
        return true;
    }

    @Override
    boolean canViewAndDownloadFiles() {
        return true;
    }

    @Override
    boolean canDeleteFiles() {
        return false;
    }

    @Override
    boolean canEditFilesComments() {
        return true;
    }

    @Override
    boolean canViewFilesComments() {
        return true;
    }

    // DISCUSSION

    @Override
    boolean canViewAdventureDiscussionWidget() {
        return true;
    }

    // OFFERS

    @Override
    boolean canViewAdventureOffersWidget() {
        return true;
    }

    @Override
    boolean canViewOfferComments() {
        return true;
    }

}
