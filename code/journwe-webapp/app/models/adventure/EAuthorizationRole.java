package models.adventure;

public enum EAuthorizationRole {
    ADVENTURE_OWNER("models.authorization.AdventureOwnerAuthorization"),ADVENTURE_PARTICIPANT("models.authorization.AdventureParticipantAuthorization"),ADVENTURE_INVITEE("models.authorization.AdventureInviteeAuthorization");

    private String className;

    EAuthorizationRole(final String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
