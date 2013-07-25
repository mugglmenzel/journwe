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

    // TIME

    // TODOLIST

    // ...
}
