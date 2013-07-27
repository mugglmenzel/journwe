package models.adventure.route;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.adventure.IAdventureComponent;
import models.adventure.JournweCloneable;

@DynamoDBTable(tableName = "journwe-routeoption")
public class RouteOption implements IAdventureComponent {

    private String adventureId;

    private String routeId;

    private String startPlaceId;

    private String endPlaceId;

    private String timeId;

    @DynamoDBHashKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBRangeKey
    @DynamoDBAutoGeneratedKey
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @DynamoDBAttribute
    public String getStartPlaceId() {
        return startPlaceId;
    }

    public void setStartPlaceId(String startPlaceId) {
        this.startPlaceId = startPlaceId;
    }

    @DynamoDBAttribute
    public String getEndPlaceId() {
        return endPlaceId;
    }

    public void setEndPlaceId(String endPlaceId) {
        this.endPlaceId = endPlaceId;
    }

    @DynamoDBAttribute
    public String getTimeId() {
        return timeId;
    }

    public void setTimeId(String timeId) {
        this.timeId = timeId;
    }
}
