package models.adventure.adventurer;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
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

    private String userIdRangeKey;

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
    public String getUserIdRangeKey() {
        return userIdRangeKey;
    }

    public void setUserIdRangeKey(String userIdRangeKey) {
        this.userIdRangeKey = userIdRangeKey;
        this.userId = userIdRangeKey;
    }

    // We need the GSI to check if a user has already created adventures
    // to identify new users who need more guidance through the website
    @DynamoDBIndexRangeKey(globalSecondaryIndexName =
            "userId-index")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.userIdRangeKey = userId;
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
