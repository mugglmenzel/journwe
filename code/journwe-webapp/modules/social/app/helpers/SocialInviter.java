package helpers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.typesafe.config.ConfigFactory;
import helpers.inviters.*;
import models.adventure.Adventure;
import models.adventure.AdventureAuthorization;
import models.adventure.EAuthorizationRole;
import models.adventure.adventurer.Adventurer;
import models.adventure.adventurer.EAdventurerParticipation;
import models.cache.CachedUserDAO;
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

import java.util.List;

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
        this.provider = provider;
        this.inviteeSocialId = inviteeSocialId;

        this.inviterSocial = provider != null ? new UserSocialDAO().findByUserIdAndProvider(provider, inviter.getId()) : new UserSocialDAO().findByUserId(inviter.getId()).iterator().next();
    }

    public void invite(String advId, String shortURL) {
        if (inviter != null && inviteeSocialId != null) {
            try {
                Adventure adv = new AdventureDAO().get(advId);
                AbstractInvitation invitation = getInvitationService();

                Logger.debug("Sending message to " + inviteeSocialId + " via " + provider + " with shortURL " + shortURL);
                invitation.send(inviteeSocialId, inviter.getName(), adv, shortURL);
            } catch (Exception e) {
                Logger.error("Could not send invitiation via " + provider, e);
            }

        }
    }


    public String createSocialInvitee(String advId) {
        AbstractInvitation invitation = getInvitationService();
        String socialEmail = null;
        String socialName = null;
        try {
            socialEmail = invitation.getInviteeEmail(inviteeSocialId);
            socialName = invitation.getInviteeName(inviteeSocialId);
        } catch (Exception e) {
            Logger.error("Could not fetch user data for invitation via " + provider, e);
        }


        UserSocial inviteeSoc = new UserSocialDAO().findBySocialId(provider, inviteeSocialId);
        if (inviteeSoc == null) {
            UserEmail ue = new UserEmailDAO().findByEmail(socialEmail);
            if (ue != null) {
                List<UserSocial> emailSocs = new UserSocialDAO().findByUserId(ue.getUserId());
                inviteeSoc = emailSocs.isEmpty() ? inviteeSoc : emailSocs.iterator().next();
            }
        }

        User invitee = inviteeSoc != null && inviteeSoc.getUserId() != null ? new UserDAO().get(inviteeSoc.getUserId()) : null;
        Logger.debug("got invitee " + invitee);
        if (invitee == null) {
            invitee = new User();
            invitee.setName(socialName);
            invitee.setRole(EUserRole.INVITEE);
            new UserDAO().save(invitee);

            Logger.debug("created invitee as user");
        }

        if (socialEmail != null && !"".equals(socialEmail) && new UserEmailDAO().get(invitee.getId(), socialEmail) == null) {
            UserEmail inviteeEmail = new UserEmail();
            inviteeEmail.setUserId(invitee.getId());
            inviteeEmail.setEmail(socialEmail);
            inviteeEmail.setPrimary(true);
            new UserEmailDAO().save(inviteeEmail);

            if (invitee.getName() == null || "".equals(invitee.getName()))
                invitee.setName(socialEmail);
        }

        if (inviteeSoc == null) {
            inviteeSoc = new UserSocial();
            inviteeSoc.setProvider(provider);
            inviteeSoc.setSocialId(inviteeSocialId);
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

        //AdventurePeopleController.clearCache(advId);
        new CachedUserDAO().clearCache(invitee.getId());

        return invitee.getId();
    }


    private AbstractInvitation getInvitationService() {
        AbstractInvitation invitation = null;

        if ("email".equals(provider)) {
            invitation = new EmailInvitation();
        } else if ("facebook".equals(provider)) {
            invitation = new FacebookInvitation(inviterSocial.getAccessToken());
        } else if ("foursquare".equals(provider)) {
            invitation = new FoursquareInvitation(inviterSocial.getSocialId(), inviterSocial.getAccessToken());
        } else if ("google".equals(provider)) {
            invitation = new GoogleInvitation(inviterSocial.getAccessToken());
        } else if ("twitter".equals(provider)) {
            invitation = new TwitterInvitation(inviterSocial.getAccessToken(), inviterSocial.getAccessSecret());
        }

        return invitation;
    }
}
