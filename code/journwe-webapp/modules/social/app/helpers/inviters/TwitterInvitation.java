package helpers.inviters;

import com.typesafe.config.ConfigFactory;
import models.adventure.Adventure;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by mugglmenzel on 05/05/14.
 */
public class TwitterInvitation extends AbstractInvitation {

    Twitter tw;

    public TwitterInvitation(String accessToken, String accessSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(ConfigFactory.load().getString("play-authenticate.twitter.consumerKey"))
                .setOAuthConsumerSecret(ConfigFactory.load().getString("play-authenticate.twitter.consumerSecret"))
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);
        this.tw = new TwitterFactory(cb.build()).getInstance();
    }


    public void send(String inviteeSocialId, String inviterName, Adventure adv, String shortURL) throws TwitterException {
        tw.directMessages().sendDirectMessage(new Long(inviteeSocialId), "Your friend " + inviterName + " wants you to join the JournWe " + adv.getName() + "! Visit " + shortURL);
    }

    public String getInviteeName(String inviteeSocialId) throws TwitterException {
        return tw.users().showUser(new Long(inviteeSocialId)).getName();
    }

    public String getInviteeEmail(String inviteeContact) throws Exception {
        return null;
    }
}
