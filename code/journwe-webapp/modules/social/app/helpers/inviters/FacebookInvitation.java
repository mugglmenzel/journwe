package helpers.inviters;

import helpers.JournweFacebookChatClient;
import helpers.JournweFacebookClient;
import models.adventure.Adventure;

/**
 * Created by mugglmenzel on 05/05/14.
 */
public class FacebookInvitation extends AbstractInvitation {

    private String accessToken;

    public FacebookInvitation(String accessToken) {
        this.accessToken = accessToken;
    }


    public void send(String inviteeSocialId, String inviterName, Adventure adv, String shortURL) {
        new JournweFacebookChatClient().sendMessage(accessToken, "You are invited to the JournWe " + adv.getName() + ". Your friend " + inviterName + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + shortURL + " to participate in that great adventure. ", inviteeSocialId);
    }

    public String getInviteeEmail(String inviteeSocialId) {
        return JournweFacebookClient.create(accessToken).getFacebookUser(inviteeSocialId).getEmail();
    }

    public String getInviteeName(String inviteeSocialId) {
        return JournweFacebookClient.create(accessToken).getFacebookUser(inviteeSocialId).getName();
    }
}
