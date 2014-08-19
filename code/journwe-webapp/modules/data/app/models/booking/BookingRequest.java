package models.booking;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "journwe-bookingrequest")
public class BookingRequest {

    private String adventureId;

    private String adventureName;

    // order booking requests by timestamp
    private Long timestamp;

    private String additionalDescription;

    @DynamoDBHashKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBRangeKey(attributeName = "timestamp")
    @DynamoDBAttribute
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long time) {
        this.timestamp = time;
    }

    @DynamoDBAttribute
    public String getAdventureName() {
        return adventureName;
    }

    public void setAdventureName(String adventureName) {
        this.adventureName = adventureName;
    }

    @DynamoDBAttribute
    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }
}
