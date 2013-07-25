package models.authorization;

public class AdventureOwnerAuthorization extends AbstractAuthorization {

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
}
