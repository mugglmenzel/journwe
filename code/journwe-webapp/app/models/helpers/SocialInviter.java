package models.helpers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.typesafe.config.ConfigFactory;
import controllers.api.json.AdventurePeopleController;
import controllers.api.json.ApplicationController;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.entities.CompactUser;
import fi.foyt.foursquare.api.entities.CompleteUser;
import fi.foyt.foursquare.api.io.DefaultIOHandler;
import models.adventure.Adventure;
import models.adventure.AdventureAuthorization;
import models.adventure.EAuthorizationRole;
import models.adventure.adventurer.Adventurer;
import models.adventure.adventurer.EAdventurerParticipation;
import models.dao.AdventureAuthorizationDAO;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserEmailDAO;
import models.dao.user.UserSocialDAO;
import models.user.EUserRole;
import models.user.User;
import models.user.UserEmail;
import models.user.UserSocial;
import play.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;

/**
 * Created by mugglmenzel on 16.02.14.
 */
public class SocialInviter {

    private static final AWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey"));


    private User inviter;

    private UserSocial inviterSocial;

    private String provider;

    private String inviteeSocialId;

    public SocialInviter(User inviter, String provider, String inviteeSocialId) {
        this.inviter = inviter;
        this.inviterSocial = new UserSocialDAO().findByUserIdAndProvider(provider, inviter.getId());
        this.provider = provider;
        this.inviteeSocialId = inviteeSocialId;
    }

    public void invite(String advId) {
        if (inviter != null && inviterSocial != null && inviteeSocialId != null) {

            Adventure adv = new AdventureDAO().get(advId);
            String inviteeEmail = "";
            String inviteeName = "";

            if ("email".equals(provider)) {
                if (inviteeSocialId != null)
                    sendSESEmail(adv, inviteeSocialId, null);

            } else if ("facebook".equals(provider)) {
                new JournweFacebookChatClient().sendMessage(inviterSocial.getAccessToken(), "You are invited to the JournWe " + adv.getName() + ". Your friend " + inviter.getName() + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + adv.getShortURL() + " to participate in that great adventure. ", inviteeSocialId);
                com.restfb.types.User inviteeFb = JournweFacebookClient.create(inviterSocial.getAccessToken()).getFacebookUser(inviteeSocialId);
                inviteeEmail = inviteeFb.getEmail();
                inviteeName = inviteeFb.getName();
            } else if ("foursquare".equals(provider)) {
                try {
                    FoursquareApi four = new FoursquareApi(ConfigFactory.load().getString("play-authenticate.foursquare.clientId"), ConfigFactory.load().getString("play-authenticate.foursquare.clientSecret"), "http://www.journwe.com" + OAuth2AuthProvider.Registry.get("foursquare").getUrl(), inviterSocial.getAccessToken(), new DefaultIOHandler());
                    CompleteUser inviteeFq = new CompleteUser();
                    for (CompactUser fu : four.usersFriends(inviterSocial.getSocialId()).getResult().getItems()) {
                        if (inviteeSocialId.equals(fu.getId())) inviteeFq = four.user(fu.getId()).getResult();
                    }

                    if (inviteeFq.getContact() != null && inviteeFq.getContact().getEmail() != null)
                        sendSESEmail(adv, inviteeFq.getContact().getEmail(), inviteeFq.getFirstName());

                    inviteeEmail = inviteeFq.getContact() != null ? inviteeFq.getContact().getEmail() : "";
                    inviteeName = inviteeFq.getFirstName() + " " + inviteeFq.getLastName();
                } catch (FoursquareApiException e) {
                    Logger.error("Could not send invitiation via Foursquare.", e);
                }
            } else if ("google".equals(provider)) {
                try {
                    GoogleCredential credential = new GoogleCredential.Builder().setClientSecrets(ConfigFactory.load().getString("play-authenticate.google.clientId"), ConfigFactory.load().getString("play-authenticate.google.clientSecret")).setTransport(new NetHttpTransport()).setJsonFactory(new JacksonFactory()).build().setFromTokenResponse(new TokenResponse().setAccessToken(inviterSocial.getAccessToken()));

                    Person inviteeGoog = new Plus(new NetHttpTransport(), new JacksonFactory(), credential).people().get(inviteeSocialId).execute();
                    String socialEmail = null;
                    for (Person.Emails e : inviteeGoog.getEmails()) {
                        socialEmail = e.getValue();
                        if (e.getType().equals("account")) break;
                    }

                    if (socialEmail != null)
                        sendSESEmail(adv, socialEmail, inviteeGoog.getDisplayName());

                    inviteeEmail = socialEmail;
                    inviteeName = inviteeGoog.getDisplayName();

                } catch (IOException e) {
                    Logger.error("Could not send invitiation via Google.", e);
                }
            } else if ("twitter".equals(provider)) {
                try {
                    ConfigurationBuilder cb = new ConfigurationBuilder();
                    cb.setDebugEnabled(true)
                            .setOAuthConsumerKey(ConfigFactory.load().getString("play-authenticate.twitter.clientId"))
                            .setOAuthConsumerSecret(ConfigFactory.load().getString("play-authenticate.twitter.clientSecret"))
                            .setOAuthAccessToken(inviterSocial.getAccessToken())
                            .setOAuthAccessTokenSecret(inviterSocial.getAccessSecret());
                    Twitter tw = new TwitterFactory(cb.build()).getInstance();
                    twitter4j.User inviteeTw = tw.users().showUser(inviteeSocialId);
                    tw.directMessages().sendDirectMessage(inviteeSocialId, "Your friend " + inviter.getName() + " wants you to join the JournWe " + adv.getName() + "! Visit " + adv.getShortURL());

                    inviteeName = inviteeTw.getName();

                } catch (TwitterException e) {
                    Logger.error("Could not send invitiation via Twitter.", e);
                }
            }

            processSocialInvitee(adv.getId(), inviteeSocialId, provider, inviteeEmail, inviteeName);
        }
    }

    private void sendSESEmail(Adventure adv, String inviteeEmail, String inviteeName) {
        new AmazonSimpleEmailServiceClient(credentials).sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(inviteeEmail)).withMessage(new Message().withSubject(new Content().withData("Invitation to the JournWe " + adv.getName())).withBody(new Body().withText(new Content().withData((inviteeName != null && !"".equals(inviteeName) ? "Hi " + inviteeName : "Hey") + ",\nYour friend " + inviter.getName() + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + adv.getShortURL() + " to participate in that great adventure.\n\nJournWe.com")))).withSource(adv.getId() + "@journwe.com").withReplyToAddresses(adv.getId() + "@journwe.com"));
    }

    private void processSocialInvitee(String advId, String inviteeId, String provider, String socialEmail, String socialName) {
        UserSocial inviteeSoc = new UserSocialDAO().findBySocialId(provider, inviteeId);
        User invitee = inviteeSoc != null && inviteeSoc.getUserId() != null ? new UserDAO().get(inviteeSoc.getUserId()) : null;
        Logger.debug("got invitee " + invitee);
        if (invitee == null) {
            invitee = new User();
            invitee.setName(socialName);
            invitee.setRole(EUserRole.INVITEE);
            new UserDAO().save(invitee);

            Logger.debug("created invitee as user");
        }

        if (new UserEmailDAO().get(invitee.getId(), socialEmail) == null) {
            UserEmail inviteeEmail = new UserEmail();
            inviteeEmail.setEmail(socialEmail);
            new UserEmailDAO().save(inviteeEmail);
        }

        if (inviteeSoc == null) {
            inviteeSoc = new UserSocial();
            inviteeSoc.setProvider(provider);
            inviteeSoc.setSocialId(inviteeId);
        }
        inviteeSoc.setUserId(invitee.getId());
        new UserSocialDAO().save(inviteeSoc);


        Adventurer inviteeAdvr = new AdventurerDAO().get(advId, invitee.getId());
        if (inviteeAdvr == null) {
            inviteeAdvr = new Adventurer();
            inviteeAdvr.setUserId(invitee.getId());
            inviteeAdvr.setAdventureId(advId);
            inviteeAdvr.setParticipationStatus(EAdventurerParticipation.INVITEE);
            new AdventurerDAO().save(inviteeAdvr);
        }

        AdventureAuthorization authorization = new AdventureAuthorization();
        authorization.setAdventureId(advId);
        authorization.setUserId(invitee.getId());
        authorization.setAuthorizationRole(EAuthorizationRole.ADVENTURE_PARTICIPANT);
        new AdventureAuthorizationDAO().save(authorization);

        AdventurePeopleController.clearCache(advId);
        ApplicationController.clearUserCache(invitee.getId());
    }
}
