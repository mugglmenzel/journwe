package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.avaje.ebean.ExpressionList;
import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.MailChimpObject;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.*;
import models.dao.common.CommonEntityDAO;
import models.user.*;
import play.Logger;
import play.api.Play;
import play.cache.Cache;
import play.mvc.Controller;
import providers.MyLoginUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthUser;
import models.LinkedAccount;

import java.io.IOException;
import java.util.Iterator;
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
                    UserSocial social = new UserSocialDAO().get(identity.getProvider(), identity.getId());
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
        Logger.debug("AuthUserIdentity: id = "+identity.getId()+" provider = "+identity.getProvider());
        return getAuthUserFind(identity);
    }

    /**
     * Find the user by his email address.
     *
     * This method is for example necessary for password recovery.
     *
     * @param email
     * @return
     */
    public static User findByEmail(final String email) {
        if (email == null || email.isEmpty())
            return null;
        String userId = getUserIdOfEmailUser(email);
        if(userId == null)
            return null;
        User toReturn = getUser(userId);
        return toReturn;
    }

    public static User findByUsernamePasswordIdentity(
            final UsernamePasswordAuthUser identity) {
        String email = identity.getEmail();
        Logger.debug("find user by email "+email);
        if(email!=null)
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
        UserSocial social = new UserSocialDAO().get(identity.getProvider(), identity.getId());
        if (social != null && authUser != null) {
            User user = new UserDAO().get(social.getUserId());
            boolean updateCache = false;

            if (EUserRole.INVITEE.equals(user.getRole())) {
                user.setRole(EUserRole.BETA);
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

            if (authUser instanceof FacebookAuthUser) {
                Logger.debug("updating access token");
                if (!((FacebookAuthUser) authUser).getOAuth2AuthInfo().getAccessToken().equals(social.getAccessToken())) {
                    social.setAccessToken(((FacebookAuthUser) authUser).getOAuth2AuthInfo().getAccessToken());
                    new UserSocialDAO().save(social);
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

    private void clearCache(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserSocial identity = new UserSocialDAO().findByUserId(userId);
                Cache.remove("user.social." + identity.getProvider() + "." + identity.getSocialId());
            }
        }).start();
    }

    public User create(final AuthUser authUser, final EUserRole role) {
        if(authUser!=null)
            Logger.debug("Create new user with id "+authUser.getId());
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

        if(new UserDAO().save(user))
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
        if(authUser.getProvider().equalsIgnoreCase(MyUsernamePasswordAuthProvider.PROVIDER_KEY))
            social.setSocialId(user.getId());
        else
            social.setSocialId(authUser.getId());
        social.setUserId(user.getId());
        if (authUser instanceof FacebookAuthUser)
            social.setAccessToken(((FacebookAuthUser) authUser).getOAuth2AuthInfo().getAccessToken());

        if(new UserSocialDAO().save(social))
            Logger.debug("Creating UserSocial with userId = "+social.getUserId() +" and social id = "+ social.getSocialId() +" was successful.");
        else
            Logger.error("Creating UserSocial failed.");

        // For MyUsernamePasswordAuthUser save Email-to-User Table
        if(authUser.getProvider().equalsIgnoreCase(MyUsernamePasswordAuthProvider.PROVIDER_KEY)) {
            UsernamePasswordAuthUser myUser = (UsernamePasswordAuthUser)authUser;
            EmailToUser etu = new EmailToUser();
            etu.setEmail(myUser.getEmail());
            etu.setProvider(myUser.getProvider());
            etu.setUserId(user.getId());
            if(new EmailToUserDAO().save(etu))
                Logger.debug("Saved email-to-user mapping between "+myUser.getEmail()+" and "+myUser.getId()+" for provider "+myUser.getProvider());
            else
                Logger.error("Saving email "+myUser.getEmail()+" to user with id "+myUser.getId()+" has failed!");
        }

        return user;
    }

    /**
     * Helper for findByEmail method
     */
    private static String getUserIdOfEmailUser(String email) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        EmailToUser etu = new EmailToUser();
        etu.setEmail(email);
        query.setHashKeyValues(etu);

        Iterator<EmailToUser> results = pm.query(EmailToUser.class, query).iterator();
        EmailToUser result = null;
        int i = 0;
        while(results.hasNext()) {
            i++;
            result = results.next();
        }
        // there should be only one result
        if(i==0)
            return null;
        if(i>1) {
            Logger.warn("Something is wrong in the UserEmailDAO.getUserIdOfEmailUser method. It should only return 1 result. But there were more, namely: ");
            while(results.hasNext()) {
                Logger.warn(results.next().getEmail()+" with user id "+results.next().getUserId());
            }
        }
        return result.getUserId();
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
        if(new UserDAO().save(unverified)) {
            new TokenActionDAO().deleteByUser(unverified, ETokenType.EMAIL_VERIFICATION);
        }
        }
    }

    public static void addLinkedAccount(final AuthUser oldUser,
                                        final AuthUser newUser) {
        final User u = new UserDAO().findByAuthUserIdentity(oldUser);
        new LinkedAccountDAO().create(u,newUser);
    }

    public void merge(final AuthUser oldUser,final AuthUser newUser) {
        final User u1 = new UserDAO().findByAuthUserIdentity(newUser);
        final User u2 = new UserDAO().findByAuthUserIdentity(oldUser);
        merge(u2,u1);
    }

    public void merge(final User oldUser,final User newUser) {
        LinkedAccountDAO ladao = new LinkedAccountDAO();
        for (final LinkedAccount lacc : ladao.all(oldUser.getId())) {
            lacc.setUserId(newUser.getId());
            ladao.save(lacc);
        }
        // do all other merging stuff here - like resources, etc.

        // deactivate the merged user that got added to this one
        oldUser.setActive(false);
        new UserDAO().save(oldUser);
    }

    // TODO
//    public void changePassword(final UsernamePasswordAuthUser authUser,
//                               final boolean create) {
//        LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
//        if (a == null) {
//            if (create) {
//                a = LinkedAccount.create(authUser);
//                a.user = this;
//            } else {
//                throw new RuntimeException(
//                        "Account not enabled for password usage");
//            }
//        }
//        a.providerUserId = authUser.getHashedPassword();
//        a.save();
//    }

    // TODO
//    public void resetPassword(final UsernamePasswordAuthUser authUser,
//                              final boolean create) {
//// You might want to wrap this into a transaction
//        this.changePassword(authUser, create);
//        new TokenActionDAO().deleteByUser(ETokenType.PASSWORD_RESET,authUser);
//    }

}
