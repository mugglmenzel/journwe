package models.adventure.time;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.adventure.EAdventurerParticipation;
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

    private ETimePreferenceVote vote;


    @DynamoDBHashKey
    public String getTimeOptionId() {
        return timeOptionId;
    }

    public void setTimeOptionId(String timeOptionId) {
        this.timeOptionId = timeOptionId;
    }

    @DynamoDBRangeKey
    public String getAdventurerId() {
        return adventurerId;
    }

    public void setAdventurerId(String adventurerId) {
        this.adventurerId = adventurerId;
    }

    @DynamoDBMarshalling(marshallerClass = VoteMarshaller.class)
    public ETimePreferenceVote getVote() {
        return vote;
    }

    public void setVote(ETimePreferenceVote vote) {
        this.vote = vote;
    }

    public static class VoteMarshaller extends EnumMarshaller<ETimePreferenceVote> {
    }
}
