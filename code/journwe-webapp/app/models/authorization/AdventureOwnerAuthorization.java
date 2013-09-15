package models.authorization;

public class AdventureOwnerAuthorization extends AbstractAuthorization {

    // ADVENTURE

    @Override
    public boolean canEditAdventureImage() {
        return true;
    }

    @Override
    public boolean canViewAdventureImage() {
        return true;
    }

    @Override
    public boolean canEditAdventureTitle() {
        return true;
    }

    @Override
    public boolean canViewAdventureTitle() {
        return true;
    }

    @Override
    public boolean canEditAdventureComments() {
        return true;
    }

    @Override
    public boolean canViewAdventureComments() {
        return true;
    }

    @Override
    public boolean canEditAdventureDescription() {
        return true;
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
        return true;
    }

    @Override
    boolean canViewPlaces() {
        return true;
    }

    @Override
    boolean canVoteForPlaces() {
        return true;
    }

    @Override
    boolean canChangeVoteOnOffForPlaces() {
        return true;
    }

    @Override
    boolean canViewVotesForPlaces() {
        return true;
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
        return true;
    }

    @Override
    boolean canViewAdventurerParticipationStatus() {
        return true;
    }

    @Override
    boolean canInviteAdventurerParticipants() {
        return true;
    }

    @Override
    boolean canAcceptAdventurerApplicants() {
        return true;
    }

    @Override
    boolean canRemoveAdventurerParticipants() {
        return true;
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

    boolean canEditDateAndTime() {
        return true;
    }

    @Override
    boolean canViewDateAndTime() {
        return true;
    }

    @Override
    boolean canVoteForDateAndTime() {
        return true;
    }

    @Override
    boolean canChangeVoteOnOffForDateAndTime() {
        return true;
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
        return true;
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
        return true;
    }

    @Override
    boolean canViewTodoItem() {
        return true;
    }

    @Override
    boolean canSetTodoListToCompleted() {
        return true;
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
        return true;
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
