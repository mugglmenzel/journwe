package models.dao.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.MailChimpObject;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.providers.oauth1.OAuth1AuthUser;
import com.feth.play.module.pa.providers.oauth1.twitter.TwitterAuthUser;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthUser;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.providers.oauth2.foursquare.FoursquareAuthUser;
import com.feth.play.module.pa.providers.oauth2.google.GoogleAuthUser;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.*;
import models.dao.SubscriberDAO;
import models.dao.common.CommonEntityDAO;
import models.user.*;
import play.Logger;
import play.cache.Cache;
import play.mvc.Controller;
import providers.MyUsernamePasswordAuthProvider;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class UserDAO extends CommonEntityDAO<User> {

    public static final String COOKIE_USER_ROLE_ON_REGISTER = "play-authenticate-user-role-on-register";
    public static final EUserRole DEFAULT_USER_ROLE = EUserRole.USER;

    public UserDAO() {
        super(User.class);
    }


    public boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity) {
        return getAuthUserFind(identity) != null;
    }


    private User getAuthUserFind(final AuthUserIdentity identity) {
        User result = null;
        try {
            result = Cache.getOrElse("user.social." + identity.getProvider() + "." + identity.getId(), new Callable<User>() {
                @Override
                public User call() throws Exception {
                    UserSocial social = new UserSocialDAO().get(identity.getId(), identity.getProvider());
                    return social != null ? new UserDAO().get(social.getUserId()) : null;
                }
            }, 3600);
        } catch (Exception e) {
            Logger.error("Error while looking up user. Ouch!", e);
        }

        return result;
    }

    public User findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null)
            return null;
        Logger.debug("AuthUserIdentity: id = " + identity.getId() + " provider = " + identity.getProvider());
        return getAuthUserFind(identity);
    }

    /**
     * Find the user by his email address.
     * <p/>
     * This method is for example necessary for password recovery.
     *
     * @param email
     * @return
     */
    public static User findByEmail(final String email) {
        User toReturn = null;
        if (email == null || email.isEmpty())
            return null;
        UserEmail key = new UserEmail();
        key.setEmail(email);
        key.setEmailRangeKey(email);

        // Query users by email address.
        List<UserEmail> emails = pm.query(UserEmail.class,
                new DynamoDBQueryExpression<UserEmail>().
                        withHashKeyValues(key).withIndexName("email-index").
                        withConsistentRead(false));
        for (UserEmail ue : emails) {
            String userId = ue.getUserId();
            List<UserSocial> socialists = new UserSocialDAO().findByUserId(userId);
            for (UserSocial us : socialists) {
                // Only return the user with password-usersocial.
                if (us.getProvider().equalsIgnoreCase("password"))
                    return getUser(userId);
            }
        }
        return toReturn;
    }

    public static User findByUsernamePasswordIdentity(
            final UsernamePasswordAuthUser identity) {
        String email = identity.getEmail();
        Logger.debug("find user by email " + email);
        if (email != null)
            return findByEmail(email);
        else {
            Logger.warn("findByUsernamePasswordIdentity(identity) identity.getEmail() is null!");
            return null;
        }
    }


    public EUserRole getRegisterUserRole() {
        Logger.debug("getting user role from cookie " + Controller.request().cookie(UserDAO.COOKIE_USER_ROLE_ON_REGISTER));
        return Controller.request().cookie(COOKIE_USER_ROLE_ON_REGISTER) != null ? EUserRole.valueOf(Controller.request().cookie(COOKIE_USER_ROLE_ON_REGISTER).value()) : DEFAULT_USER_ROLE;
    }


    public void update(final AuthUser authUser, final AuthUserIdentity identity) {
        UserSocial social = new UserSocialDAO().get(identity.getId(), identity.getProvider());
        if (social != null && authUser != null) {
            User user = new UserDAO().get(social.getUserId());
            boolean updateCache = false;

            if (EUserRole.INVITEE.equals(user.getRole())) {
                user.setRole(getRegisterUserRole());
                Logger.debug("switch from INVITEE to " + getRegisterUserRole());
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
            } else if(authUser instanceof OAuth1AuthUser) {
                Logger.debug("updating oauth1 access token");
                if (!((OAuth1AuthUser) authUser).getOAuth1AuthInfo().getAccessToken().equals(social.getAccessToken())) {
                    social.setAccessToken(((OAuth1AuthUser) authUser).getOAuth1AuthInfo().getAccessToken());
                    social.setAccessSecret(((OAuth1AuthUser) authUser).getOAuth1AuthInfo().getAccessTokenSecret());
                    new UserSocialDAO().save(social);
                    updateCache = true;
                }
            }

            save(user);
            if (updateCache) clearCache(user.getId());
        }
    }

    public void updateRole(User user, EUserRole role) {
        user.setRole(role);
        save(user);
        clearCache(user.getId());
    }

    public void clearCache(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (UserSocial identity : new UserSocialDAO().findByUserId(userId))
                    Cache.remove("user.social." + identity.getProvider() + "." + identity.getSocialId());
            }
        }).start();
    }

    public User create(final AuthUser authUser, final EUserRole role) {
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

    /**
     * Helper
     */
    private static User getUser(String userId) {
        return new UserDAO().get(userId);
    }

    // User Password stuff

    public static void verify(final User unverified) {
// You might want to wrap this into a transaction
        final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(unverified.getId());
        ue.setValidated(true);
        if (new UserEmailDAO().save(ue)) {
            if (new UserDAO().save(unverified)) {
                new TokenActionDAO().deleteByUser(unverified, ETokenType.EMAIL_VERIFICATION);
            }
        }
    }

    public void merge(final AuthUser oldAuthUser, final AuthUser newAuthUser) {
        UserSocialDAO usdao = new UserSocialDAO();
        UserSocial us = usdao.get(oldAuthUser.getProvider(), oldAuthUser.getId());
        final User newUser = new UserDAO().findByAuthUserIdentity(newAuthUser);
        us.setUserId(newUser.getId());
        usdao.save(us);
        // do all other merging stuff here - like resources, etc.
        // TODO just throw away the adventures etc. of the other user?
        // deactivate the merged user that got added to this one
        final User oldUser = new UserDAO().findByAuthUserIdentity(oldAuthUser);
        oldUser.setActive(false);
        new UserDAO().save(oldUser);
    }

    public void changePassword(final UsernamePasswordAuthUser authUser,
                               final boolean create) {
        UserSocial us = new UserSocialDAO().findBySocialId(authUser.getProvider(), authUser.getId());
        if (us == null) {
            if (create) {
                us = new UserSocialDAO().create(authUser);
            } else {
                throw new RuntimeException(
                        "account not enabled for password usage");
            }
        }
        User u = new UserDAO().findByAuthUserIdentity(authUser);
        Logger.debug("Set hashed password: " + authUser.getHashedPassword());
        u.setHashedPassword(authUser.getHashedPassword());
        new UserDAO().save(u);
        us.setUserId(u.getId());
        new UserSocialDAO().save(us);
    }

    public void resetPassword(final UsernamePasswordAuthUser authUser,
                              final boolean create) {
        // You might want to wrap this into a transaction
        this.changePassword(authUser, create);
        User user = new UserDAO().findByAuthUserIdentity(authUser);
        new TokenActionDAO().deleteByUser(user, ETokenType.PASSWORD_RESET);
    }

}
