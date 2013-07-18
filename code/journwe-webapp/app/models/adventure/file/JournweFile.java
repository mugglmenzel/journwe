package models.adventure.file;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.dao.UserDAO;
import models.dao.UserSocialDAO;
import models.user.User;
import models.user.UserSocial;
import play.data.validation.Constraints.Required;

@DynamoDBTable(tableName = "journwe-file")
public class JournweFile {

    private String adventureId;

    @Required
    private String fileName;

    private String fileDescription;

    private String url;

    private String storageProvider;

    private String userId;

    @DynamoDBHashKey(attributeName = "adventureId")
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBRangeKey(attributeName = "fileName")
    public String getFileName() {
        return fileName;
    }

    @DynamoDBAttribute
    public String getStorageProvider() {
        return storageProvider;
    }

    @DynamoDBAttribute
    public String getUserId() {
        return userId;
    }

    @DynamoDBAttribute
    public String getUrl() {
        return url;
    }

    @DynamoDBAttribute
    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setStorageProvider(String storageProvider) {
        this.storageProvider = storageProvider;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @DynamoDBIgnore
    public User getUser() {
        UserSocial social = new UserSocialDAO().findBySocialId(this.getUserId());

        if (social != null) {
            return new UserDAO().get(social.getUserId());
        }
        return null;
    }
}
