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
public class PlacePreference {

    private String placeOptionId;

    private String userId;

    private EPreferenceVote vote = EPreferenceVote.MAYBE;

    private Double voteGravity = 0.5D;

    @DynamoDBHashKey(attributeName = "placeoptionid")
    public String getPlaceOptionId() {
        return placeOptionId;
    }

    public void setPlaceOptionId(String placeOptionId) {
        this.placeOptionId = placeOptionId;
    }

    @DynamoDBRangeKey(attributeName = "adventurerid")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBMarshalling(marshallerClass = VoteMarshaller.class)
    public EPreferenceVote getVote() {
        return vote;
    }

    public void setVote(EPreferenceVote vote) {
        this.vote = vote;
    }

    @DynamoDBAttribute
    public Double getVoteGravity() {
        return voteGravity;
    }

    public void setVoteGravity(Double voteGravity) {
        this.voteGravity = voteGravity;
    }

    public static class VoteMarshaller extends EnumMarshaller<EPreferenceVote> {
    }

}
