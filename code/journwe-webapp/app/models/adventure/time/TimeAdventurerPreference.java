package models.adventure.time;

import models.adventure.EAdventurerParticipation;
import models.helpers.EnumMarshaller;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 03.06.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class TimeAdventurerPreference {

    private String adventureId;

    private String adventurerId;

    private ETimePreferenceVote vote;


    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    public String getAdventurerId() {
        return adventurerId;
    }

    public void setAdventurerId(String adventurerId) {
        this.adventurerId = adventurerId;
    }

    public ETimePreferenceVote getVote() {
        return vote;
    }

    public void setVote(ETimePreferenceVote vote) {
        this.vote = vote;
    }

    public static class VoteMarshaller extends EnumMarshaller<ETimePreferenceVote> {
    }
}
