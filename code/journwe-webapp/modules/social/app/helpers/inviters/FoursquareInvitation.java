package helpers.inviters;

import com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;
import com.typesafe.config.ConfigFactory;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.entities.CompactUser;
import fi.foyt.foursquare.api.entities.CompleteUser;
import fi.foyt.foursquare.api.io.DefaultIOHandler;

/**
 * Created by mugglmenzel on 05/05/14.
 */
public class FoursquareInvitation extends AbstractInvitation {
    private FoursquareApi four;

    private String inviterSocialId;

    public FoursquareInvitation(String inviterSocialId, String accessToken) {
        this.inviterSocialId = inviterSocialId;
        this.four = new FoursquareApi(ConfigFactory.load().getString("play-authenticate.foursquare.clientId"), ConfigFactory.load().getString("play-authenticate.foursquare.clientSecret"), "http://www.journwe.com" + OAuth2AuthProvider.Registry.get("foursquare").getUrl(), accessToken, new DefaultIOHandler());

    }

    public String getInviteeName(String inviteeSocialId) throws FoursquareApiException {
        CompleteUser inviteeFq = new CompleteUser();
        for (CompactUser fu : four.usersFriends(inviterSocialId).getResult().getItems()) {
            if (inviteeSocialId.equals(fu.getId())) inviteeFq = four.user(fu.getId()).getResult();
        }

        return inviteeFq.getFirstName() + " " + inviteeFq.getLastName();
    }

    public String getInviteeEmail(String inviteeSocialId) throws FoursquareApiException {
        CompleteUser inviteeFq = new CompleteUser();
        for (CompactUser fu : four.usersFriends(inviterSocialId).getResult().getItems()) {
            if (inviteeSocialId.equals(fu.getId())) inviteeFq = four.user(fu.getId()).getResult();
        }

        return inviteeFq.getContact() != null ? inviteeFq.getContact().getEmail() : null;
    }
}
