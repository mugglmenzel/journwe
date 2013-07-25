package models.authorization;

public class AdventureInviteeAuthorization extends AbstractAuthorization {

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
}
