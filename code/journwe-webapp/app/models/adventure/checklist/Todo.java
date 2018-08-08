package models.adventure.checklist;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import models.adventure.IAdventureComponentWithUser;
import models.adventure.JournweCloneable;
import models.dao.helpers.EnumMarshaller;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 15.04.13
 * Time: 08:29
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-todo")
public class Todo implements IAdventureComponentWithUser {

    private String id;

    private String adventureId;

    private String userId;

    @JournweCloneable
    private String title;

    private EStatus status = EStatus.NEW;

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

    @DynamoDBRangeKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBMarshalling(marshallerClass = StatusMarshaller.class)
    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public static class StatusMarshaller extends EnumMarshaller<EStatus> {
    }
}