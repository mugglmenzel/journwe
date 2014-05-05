package helpers.inviters;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

/**
 * Created by mugglmenzel on 05/05/14.
 */
public class GoogleInvitation extends AbstractInvitation {

    private GoogleCredential credential;

    public GoogleInvitation(String accessToken) {
        this.credential = new GoogleCredential.Builder().setClientSecrets(ConfigFactory.load().getString("play-authenticate.google.clientId"), ConfigFactory.load().getString("play-authenticate.google.clientSecret")).setTransport(new NetHttpTransport()).setJsonFactory(new JacksonFactory()).build().setFromTokenResponse(new TokenResponse().setAccessToken(accessToken));

    }

    public String getInviteeName(String inviteeSocialId) throws IOException {
        Person inviteeGoog = new Plus(new NetHttpTransport(), new JacksonFactory(), credential).people().get(inviteeSocialId).execute();
        return inviteeGoog.getDisplayName();
    }

    public String getInviteeEmail(String inviteeSocialId) throws IOException {
        Person inviteeGoog = new Plus(new NetHttpTransport(), new JacksonFactory(), credential).people().get(inviteeSocialId).execute();
        String socialEmail = null;
        for (Person.Emails e : inviteeGoog.getEmails()) {
            socialEmail = e.getValue();
            if (e.getType().equals("account")) break;
        }

        return socialEmail;
    }
}
