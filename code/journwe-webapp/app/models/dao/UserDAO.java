package models.dao;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.user.*;
import models.dao.common.CommonEntityDAO;
import models.user.*;
import play.Logger;
import play.api.Play;
import play.cache.Cache;
import play.mvc.Controller;

import java.io.IOException;
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
            }, 24 * 3600);
        } catch (Exception e) {
            Logger.error("Error while looking up user. Ouch!", e);
        }

        return result;
    }

    public User findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null)
            return null;

        return getAuthUserFind(identity);
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

        new UserDAO().save(user);


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
        social.setSocialId(authUser.getId());
        social.setUserId(user.getId());
        if (authUser instanceof FacebookAuthUser)
            social.setAccessToken(((FacebookAuthUser) authUser).getOAuth2AuthInfo().getAccessToken());

        new UserSocialDAO().save(social);

        return user;
    }

}
