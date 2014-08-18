package models.authorization;

public class AdventureInviteeAuthorization extends AbstractAuthorization {

    // ADVENTURE

    @Override
    public boolean canViewAdventureImage() {
        return true;
    }

    @Override
    public boolean canViewAdventureTitle() {
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
    boolean canViewPlaces() {
        return true;
    }

    @Override
    boolean canViewFavoritePlace() {
        return true;
    }

    // ADVENTURERS

    // ... can see / edit nothing

    // TIME

    @Override
    boolean canViewAdventureTimeWidget() {
        return true;
    }

    @Override
    boolean canViewDateAndTime() {
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

    // TODOLIST

    // .. can see / edit nothing

    // FILES

    // .. can see / edit nothing

    // DISCUSSION

    // .. can see / post nothing

    // OFFERS

    // .. can see nothing

    // BOOKING

    // .. can see nothing
}
