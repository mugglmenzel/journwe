package models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.helpers.WeatherFactory;
import play.data.validation.Constraints.Required;

import java.util.Date;

@DynamoDBTable(tableName = "journwe-inspiration")
public class Inspiration {

    private String inspirationId;

    private String categoryId;

    @Required
    private String name;

    private String description;

    private String image;

    private String placeAddress;

    private Double placeLatitude;

    private Double placeLongitude;

    private Date timeStart;

    private Date timeEnd;

    // Tools

    private String tripAdvisor;

    private String pinterest;

    private String weatherId;


    @DynamoDBIgnore
    public static Inspiration fromId(String id) {
        if (id == null || !id.contains(":")) return null;
        Inspiration po = new Inspiration();
        po.setCategoryId(id.split(":")[0]);
        po.setInspirationId(id.split(":")[1]);
        return po;
    }

    @DynamoDBIgnore
    public String getId() {
        return categoryId + ":" + inspirationId;
    }

    /**
     * @return the categoryId
     */
    @DynamoDBHashKey(attributeName = "categoryId")
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the inspirationId
     */
    @DynamoDBRangeKey(attributeName = "inspirationId")
    @DynamoDBAutoGeneratedKey
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

    /**
     * @return the description
     */
    @DynamoDBAttribute
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the image
     */
    @DynamoDBAttribute
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    @DynamoDBAttribute
    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    @DynamoDBAttribute
    public Double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(Double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    @DynamoDBAttribute
    public Double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(Double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    @DynamoDBAttribute
    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    @DynamoDBAttribute
    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * @return the image
     */
    @DynamoDBAttribute
    public String getTripAdvisor() {
        return tripAdvisor;
    }

    /**
     * @param ta Provides a snipped for trip advisor
     */
    public void setTripAdvisor(String ta) {
        this.tripAdvisor = ta;
    }

    /**
     * @return the image
     */
    @DynamoDBAttribute
    public String getPinterest() {
        return pinterest;
    }

    /**
     * @param pinterest Provides a snipped for trip advisor
     */
    public void setPinterest(String pinterest) {
        this.pinterest = pinterest;
    }

    /**
     *
     */
    @DynamoDBIgnore
    public Weather getWeather() {
        if (this.weatherId == null) {
            return null;
        }
        return WeatherFactory.getWeather(this.weatherId);
    }

    /**
     *
     */
    @DynamoDBAttribute
    public String getWeatherId() {
        return this.weatherId;
    }

    /**
     *
     */
    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Inspiration [categoryId=" + categoryId
                + ", inspirationId=" + inspirationId + ", name=" + name + ", description="
                + description + ", image=" + image + "]";
    }


}
