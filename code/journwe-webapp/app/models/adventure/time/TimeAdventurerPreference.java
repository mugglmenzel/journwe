package models.adventure.time;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.adventure.EPreferenceVote;
import models.helpers.EnumMarshaller;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 03.06.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-timepreference")
public class TimeAdventurerPreference {

    private String timeOptionId;

    private String adventurerId;

    private EPreferenceVote vote = EPreferenceVote.MAYBE;


    @DynamoDBHashKey(attributeName = "timeoptionid")
    public String getTimeOptionId() {
        return timeOptionId;
    }

    public void setTimeOptionId(String timeOptionId) {
        this.timeOptionId = timeOptionId;
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
