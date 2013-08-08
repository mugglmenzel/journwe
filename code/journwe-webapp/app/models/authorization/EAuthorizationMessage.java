package models.authorization;

public enum EAuthorizationMessage {
    NO_AUTHORIZATION("Sorry. You are not authorized.");

    private final String text;

    private EAuthorizationMessage(String s) {
        text = s;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:text.equals(otherName);
    }

    public String toString(){
        return text;
    }
}
