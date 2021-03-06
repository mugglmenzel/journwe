package models.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import play.data.validation.Constraints.Required;

@DynamoDBTable(tableName = "journwe-adventure")
public class Adventure {

    private String id;

    @JournweCloneable
    private String inspirationId;

    @Required
    private String name;

    @JournweCloneable
    private String description;

    private String image;

    private String shortURL;

    private boolean publish = false;

    private boolean limited = false;

    private Integer limit;

    private Boolean placeVoteOpen = true;

    private String favoritePlaceId;

    private Boolean timeVoteOpen = true;

    private String favoriteTimeId;

    /**
     * @return the inspirationId
     */
    @DynamoDBAttribute(attributeName = "inspirationId")
    public String getInspirationId() {
        return inspirationId;
    }

    /**
     * @param inspirationId the inspirationId to set
     */
    public void setInspirationId(String inspirationId) {
        this.inspirationId = inspirationId;
    }

    /**
     * @return the id
     */
    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    @DynamoDBAttribute
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute
    public String getDescription() {
        return description;
    }

    @DynamoDBIgnore
    public String getHTMLDescription(){
        return description == null ? "" : description.replaceAll("\n", "<br/>");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @DynamoDBAttribute
    public String getShortURL() {
        return shortURL;
    }

    public void setShortURL(String shortURL) {
        this.shortURL = shortURL;
    }

    /**
     * @return the publish
     */
    @DynamoDBAttribute
    public boolean isPublish() {
        return publish;
    }

    /**
     * @param publish the publish to set
     */
    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    /**
     * @return the limited
     */
    @DynamoDBAttribute
    public boolean isLimited() {
        return limited;
    }

    /**
     * @param limited the limited to set
     */
    public void setLimited(boolean limited) {
        this.limited = limited;
    }

    /**
     * @return the limit
     */
    @DynamoDBAttribute
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getFavoritePlaceId() {
        return favoritePlaceId;
    }

    public void setFavoritePlaceId(String favoritePlaceId) {
        this.favoritePlaceId = favoritePlaceId;
    }

    public String getFavoriteTimeId() {
        return favoriteTimeId;
    }

    public void setFavoriteTimeId(String favoriteTimeId) {
        this.favoriteTimeId = favoriteTimeId;
    }

    public Boolean getPlaceVoteOpen() {
        return placeVoteOpen;
    }

    public void setPlaceVoteOpen(Boolean placeVoteOpen) {
        this.placeVoteOpen = placeVoteOpen;
    }

    public Boolean getTimeVoteOpen() {
        return timeVoteOpen;
    }

    public void setTimeVoteOpen(Boolean timeVoteOpen) {
        this.timeVoteOpen = timeVoteOpen;
    }

    @Override
    public String toString() {
        return "Adventure{" +
                "id='" + id + '\'' +
                ", inspirationId='" + inspirationId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", publish=" + publish +
                ", limited=" + limited +
                ", limit=" + limit +
                '}';
    }
}
