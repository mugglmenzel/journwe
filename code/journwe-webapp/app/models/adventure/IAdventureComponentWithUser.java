package models.adventure;

public interface IAdventureComponentWithUser extends IAdventureComponent {
    String getUserId();
    void setUserId(String userId);
}
