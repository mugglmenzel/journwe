package models.authorization;

public abstract class AbstractAuthorization {
    // ADVENTURE INFO
    boolean canEditAdventureImage() {
        return false;
    }
    boolean canViewAdventureImage() {
        return false;
    }
    boolean canEditAdventureTitle() {
        return false;
    }
    boolean canViewAdventureTitle() {
        return false;
    }
    boolean canEditAdventureComments() {
        return false;
    }
    boolean canViewAdventureComments() {
        return false;
    }
    boolean canEditAdventureDescription() {
        return false;
    }
    boolean canViewAdventureDescription() {
        return false;
    }

    // PLACE
    boolean canEditPlaces() {
        return false;
    }
    boolean canViewPlaces() {
        return false;
    }
    boolean canVoteForPlaces() {
        return false;
    }
    boolean canChangeVoteOnOffForPlaces() {
        return false;
    }
    boolean canViewVotesForPlaces() {
        return false;
    }
    boolean canViewFavoritePlace() {
        return false;
    }
    boolean canEditPlaceComments() {
        return false;
    }
    boolean canViewPlaceComments() {
        return false;
    }

    // ADVENTURERS
    boolean canViewAdventurerParticipants() {
        return false;
    }
    boolean canEditAdventurerParticipationStatus() {
        return false;
    }
    boolean canViewAdventurerParticipationStatus() {
        return false;
    }
    boolean canInviteAdventurerParticipants() {
        return false;
    }
    boolean canAcceptAdventurerApplicants() {
        return false;
    }
    boolean canRemoveAdventurerParticipants() {
        return false;
    }
    boolean canEditAdventurerComments() {
        return false;
    }
    boolean canViewAdventurerComments() {
        return false;
    }

    // TIME
    boolean canEditDateAndTime() {
        return false;
    }
    boolean canViewDateAndTime() {
        return false;
    }
    boolean canVoteForDateAndTime() {
        return false;
    }
    boolean canChangeVoteOnOffForDateAndTime() {
        return false;
    }
    boolean canViewVotesForDateAndTime() {
        return false;
    }
    boolean canViewFavoriteDateAndTime() {
        return false;
    }
    boolean canEditDateAndTimeComments() {
        return false;
    }
    boolean canViewDateAndTimeComments() {
        return false;
    }

    // TODOLIST

    boolean canEditTodoItem() {
        return false;
    }
    boolean canViewTodoItem() {
        return false;
    }
    boolean canSetTodoListToCompleted() {
        return false;
    }
    boolean canEditTodoComments() {
        return false;
    }
    boolean canViewTodoComments() {
        return false;
    }

    // FILE
    boolean canUploadFiles() {
        return false;
    }
    boolean canViewAndDownloadFiles() {
        return false;
    }
    boolean canEditFilesComments() {
        return false;
    }
    boolean canViewFilesComments() {
        return false;
    }

}
