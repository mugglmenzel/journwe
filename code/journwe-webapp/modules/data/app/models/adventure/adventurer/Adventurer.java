package models.adventure.adventurer;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.databind.JsonNode;
import models.adventure.IAdventureComponentWithUser;
import models.dao.helpers.EnumMarshaller;
import play.libs.Json;

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

    private EAdventurerRole role;

    private String visibleSectionsJSON;

    private String emergencyContact;

    private String homeAirport;

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
    @DynamoDBIndexHashKey(globalSecondaryIndexName =
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

    @DynamoDBMarshalling(marshallerClass = RoleMarshaller.class)
    public EAdventurerRole getRole() {
        return role;
    }

    public void setRole(EAdventurerRole role) {
        this.role = role;
    }

    @DynamoDBAttribute
    public String getVisibleSectionsJSON() {
        return visibleSectionsJSON;
    }

    public void setVisibleSectionsJSON(String visibleSectionsJSON) {
        this.visibleSectionsJSON = visibleSectionsJSON;
    }

    @DynamoDBAttribute
    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    @DynamoDBAttribute
    public String getHomeAirport() {
        return homeAirport;
    }

    public void setHomeAirport(String homeAirport) {
        this.homeAirport = homeAirport;
    }

    public static class ParticipationMarshaller extends EnumMarshaller<EAdventurerParticipation> {
    }
    public static class RoleMarshaller extends EnumMarshaller<EAdventurerRole> {
    }
}
