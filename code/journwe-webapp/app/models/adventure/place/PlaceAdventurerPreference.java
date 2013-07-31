package models.adventure.place;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.adventure.EPreferenceVote;
import models.dao.helpers.EnumMarshaller;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 02.07.13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-placepreference")
public class PlaceAdventurerPreference {

    private String placeOptionId;

    private String adventurerId;

    private EPreferenceVote vote = EPreferenceVote.MAYBE;

    @DynamoDBHashKey(attributeName = "placeoptionid")
    public String getPlaceOptionId() {
        return placeOptionId;
    }

    public void setPlaceOptionId(String placeOptionId) {
        this.placeOptionId = placeOptionId;
    }

    @DynamoDBRangeKey(attributeName = "adventurerid")
    public String getAdventurerId() {
        return adventurerId;
    }

    public void setAdventurerId(String adventurerId) {
        this.adventurerId = adventurerId;
    }

    @DynamoDBMarshalling(marshallerClass = VoteMarshaller.class)
    public EPreferenceVote getVote() {
        return vote;
    }

    public void setVote(EPreferenceVote vote) {
        this.vote = vote;
    }

    public static class VoteMarshaller extends EnumMarshaller<EPreferenceVote> {
    }

}
