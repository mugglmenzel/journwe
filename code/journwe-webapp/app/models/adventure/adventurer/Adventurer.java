package models.adventure.adventurer;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import models.adventure.IAdventureComponentWithUser;
import models.dao.helpers.EnumMarshaller;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 15.04.13
 * Time: 08:29
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-adventurer")
public class Adventurer implements IAdventureComponentWithUser {

    private String adventureId;

    private String userId;

    private EAdventurerParticipation participationStatus = EAdventurerParticipation.APPLICANT;

    @DynamoDBHashKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBRangeKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBMarshalling(marshallerClass = ParticipationMarshaller.class)
    public EAdventurerParticipation getParticipationStatus() {
        return participationStatus;
    }

    public void setParticipationStatus(EAdventurerParticipation participationStatus) {
        this.participationStatus = participationStatus;
    }


    public static class ParticipationMarshaller extends EnumMarshaller<EAdventurerParticipation> {
    }
}
