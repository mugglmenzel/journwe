package models;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.MailChimpObject;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.providers.oauth1.OAuth1AuthUser;
import com.feth.play.module.pa.providers.oauth1.twitter.TwitterAuthUser;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthUser;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.providers.oauth2.foursquare.FoursquareAuthUser;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.*;
import models.cache.CachedUserDAO;
import models.dao.SubscriberDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserEmailDAO;
import models.dao.user.UserSocialDAO;
import models.providers.MyUsernamePasswordAuthProvider;
import models.user.*;
import play.Logger;

import java.io.IOException;

/**
 * Created by mugglmenzel on 21/02/14.
 */
public class UserManager {

    public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null)
            return null;
        Logger.debug("AuthUserIdentity: id = " + identity.getId() + ", provider = " + identity.getProvider());
        return getAuthUserFind(identity);
    }

    public static boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity) {
        return getAuthUserFind(identity) != null;
    }


    private static User getAuthUserFind(final AuthUserIdentity identity) {
        return new CachedUserDAO().getUserBySocial(identity.getProvider(), identity.getId());
    }

    public static User findByUsernamePasswordIdentity(
            final UsernamePasswordAuthUser identity) {
        String email = identity.getEmail();
        Logger.debug("find user by email " + email);
        if (email != null)
            return new UserDAO().findByEmail(email);
        else {
            Logger.warn("findByUsernamePasswordIdentity(identity) identity.getEmail() is null!");
            return null;
        }
    }

    public static User create(final AuthUser authUser, final EUserRole role) {
        if (authUser != null)
            Logger.debug("Create new user with id " + authUser.getId());
        else
            Logger.debug("Create new user.");

        final User user = new User();
        user.setActive(true);
        user.setRole(role);
        if (authUser instanceof NameIdentity && ((NameIdentity) authUser).getName() != null) {
            user.setName(((NameIdentity) authUser).getName());
        }
        if (authUser instanceof PicturedIdentity) {
            String picture = "";
            if (authUser instanceof FacebookAuthUser)
                picture = ((PicturedIdentity) authUser).getPicture() + "?type=large";
            else picture = ((PicturedIdentity) authUser).getPicture();
            user.setImage(picture);
        }

        if (new UserDAO().save(user))
            Logger.debug("Saved user.");
        else
            Logger.error("Saving user in method UserDAO.create failed.");

        if (authUser instanceof EmailIdentity) {
            final UserEmail email = new UserEmail();
            email.setUserId(user.getId());

            final EmailIdentity identity = (EmailIdentity) authUser;
            // Remember, even when getting them from FB & Co., emails should be
            // verified within the application as a security breach there might
            // break your security as well!
            email.setEmail(identity.getEmail());
            email.setEmailRangeKey(identity.getEmail());
            email.setValidated(false);
            email.setPrimary(true);
            new UserEmailDAO().save(email);

            Subscriber sub = new Subscriber();
            sub.setEmail(email.getEmail());
            new SubscriberDAO().save(sub);
            try {
                ListSubscribeMethod listSubscribeMethod = new ListSubscribeMethod();
                listSubscribeMethod.apikey = "426c4fc75113db8416df74f92831d066-us4";
                listSubscribeMethod.id = "c18d5a32fb";
                listSubscribeMethod.email_address = sub.getEmail();
                listSubscribeMethod.double_optin = false;
                listSubscribeMethod.update_existing = true;
                listSubscribeMethod.send_welcome = true;

                new MailChimpClient().execute(listSubscribeMethod);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (MailChimpException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        final UserSocial social = new UserSocial();
        social.setProvider(authUser.getProvider());
        if (authUser.getProvider().equalsIgnoreCase(MyUsernamePasswordAuthProvider.PROVIDER_KEY))
            social.setSocialId(user.getId());
        else
            social.setSocialId(authUser.getId());
        social.setUserId(user.getId());
        if (authUser instanceof FacebookAuthUser)
            social.setAccessToken(((FacebookAuthUser) authUser).getOAuth2AuthInfo().getAccessToken());
        if (authUser instanceof FoursquareAuthUser)
            social.setAccessToken(((FoursquareAuthUser) authUser).getOAuth2AuthInfo().getAccessToken());
        if (authUser instanceof TwitterAuthUser)
            social.setAccessToken(((TwitterAuthUser) authUser).getOAuth1AuthInfo().getAccessToken());

        if (new UserSocialDAO().save(social))
            Logger.debug("Creating UserSocial with userId = " + social.getUserId() + " and social id = " + social.getSocialId() + " was successful.");
        else
            Logger.error("Creating UserSocial failed.");

        return user;
    }

    public static void update(final AuthUser authUser, final AuthUserIdentity identity) {
        UserSocial social = new UserSocialDAO().get(identity.getId(), identity.getProvider());
        if (social != null && authUser != null) {
            User user = new UserDAO().get(social.getUserId());
            boolean updateCache = false;

            if (EUserRole.INVITEE.equals(user.getRole())) {
                user.setRole(new UserDAO().getRegisterUserRole());
                Logger.debug("switch from INVITEE to " + new UserDAO().getRegisterUserRole());
                updateCache = true;
            }

            if (authUser instanceof NameIdentity && ((NameIdentity) authUser).getName() != null && !((NameIdentity) authUser).getName().equals(user.getName())) {
                Logger.debug("updating name");
                user.setName(((NameIdentity) authUser).getName());
                updateCache = true;
            }


            if (authUser instanceof EmailIdentity) {
                UserEmail userEmail = new UserEmailDAO().getPrimaryEmailOfUser(social.getUserId());
                if (userEmail == null || !userEmail.equals(((EmailIdentity) authUser).getEmail())) {
                    UserEmail email = new UserEmailDAO().get(social.getUserId(), ((EmailIdentity) authUser).getEmail());

                    if (email == null) {
                        email = new UserEmail();
                        email.setUserId(user.getId());
                        email.setEmail(((EmailIdentity) authUser).getEmail());
                        email.setValidated(false);
                        email.setPrimary(userEmail == null);
                        new UserEmailDAO().save(email);

                        if (userEmail == null) {
                            Subscriber sub = new Subscriber();
                            sub.setEmail(email.getEmail());
                            new SubscriberDAO().save(sub);
                            try {
                                ListSubscribeMethod listSubscribeMethod = new ListSubscribeMethod();
                                listSubscribeMethod.apikey = "426c4fc75113db8416df74f92831d066-us4";
                                listSubscribeMethod.id = "c18d5a32fb";
                                listSubscribeMethod.email_address = sub.getEmail();
                                MailChimpObject merge_vars = new MailChimpObject();
                                merge_vars.put("FNAME", user.getName());
                                listSubscribeMethod.merge_vars = merge_vars;
                                listSubscribeMethod.double_optin = false;
                                listSubscribeMethod.update_existing = true;
                                listSubscribeMethod.send_welcome = true;

                                new MailChimpClient().execute(listSubscribeMethod);
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } catch (MailChimpException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    }
                }

            }

            if (authUser instanceof PicturedIdentity) {
                Logger.debug("updating picture");

                String picture = "";
                if (authUser instanceof FacebookAuthUser)
                    picture = ((PicturedIdentity) authUser).getPicture() + "?width=1200";
                else picture = ((PicturedIdentity) authUser).getPicture();
                if (!picture.equals(user.getImage())) {
                    user.setImage(picture);
                    updateCache = true;
                }
            }

            if (authUser instanceof OAuth2AuthUser) {
                Logger.debug("updating oauth2 access token");
                if (!((OAuth2AuthUser) authUser).getOAuth2AuthInfo().getAccessToken().equals(social.getAccessToken())) {
                    social.setAccessToken(((OAuth2AuthUser) authUser).getOAuth2AuthInfo().getAccessToken());
                    social.setRefreshToken(((OAuth2AuthUser) authUser).getOAuth2AuthInfo().getRefreshToken());
                    new UserSocialDAO().save(social);
                    updateCache = true;
                }
            } else if (authUser instanceof OAuth1AuthUser) {
                Logger.debug("updating oauth1 access token");
                if (!((OAuth1AuthUser) authUser).getOAuth1AuthInfo().getAccessToken().equals(social.getAccessToken())) {
                    social.setAccessToken(((OAuth1AuthUser) authUser).getOAuth1AuthInfo().getAccessToken());
                    social.setAccessSecret(((OAuth1AuthUser) authUser).getOAuth1AuthInfo().getAccessTokenSecret());
                    new UserSocialDAO().save(social);
                    updateCache = true;
                }
            }

            new UserDAO().save(user);
            if (updateCache) new CachedUserDAO().clearCache(user.getId());
        }
    }

    public static void updateRole(User user, EUserRole role) {
        user.setRole(role);
        new UserDAO().save(user);
        new CachedUserDAO().clearCache(user.getId());
    }

    public static void changePassword(AuthUser usr, String newPassword, boolean create) {
        new UserDAO().changePassword(usr.getProvider(), usr.getId(), newPassword, create);
    }

    public static void resetPassword(AuthUser u, String newPassword, boolean create) {
        new UserDAO().resetPassword(u.getProvider(), u.getId(), newPassword, create);
    }


    public static void merge(final AuthUser oldAuthUser, final AuthUser newAuthUser) {
        new UserDAO().merge(oldAuthUser.getProvider(), oldAuthUser.getId(), newAuthUser.getProvider(), newAuthUser.getId());
    }

    public static boolean addLinkedAccount(final AuthUser oldAuthUser, final AuthUser newAuthUser) {
        User usr = new UserDAO().findByProviderAndSocialId(oldAuthUser.getProvider(), oldAuthUser.getId());
        new CachedUserDAO().clearCache(usr.getId());
        return new UserSocialDAO().addLinkedAccount(usr, newAuthUser.getProvider(), newAuthUser.getId());
    }

}
