package models.dao;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.method.list.ListSubscribeMethod;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import models.dao.common.CommonEntityDAO;
import models.user.Subscriber;
import models.user.User;
import models.user.UserEmail;
import models.user.UserSocial;

import java.io.IOException;

public class UserDAO extends CommonEntityDAO<User> {

    public UserDAO() {
        super(User.class);
    }


    public boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity) {
        return getAuthUserFind(identity) != null;
    }

    private User getAuthUserFind(final AuthUserIdentity identity) {
        UserSocial social = new UserSocialDAO().get(identity.getProvider(), identity.getId());
        return social != null ? new UserDAO().get(social.getUserId()) : null;
    }

    public User findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null)
            return null;

        return getAuthUserFind(identity);
    }

    public User create(final AuthUser authUser) {
        final User user = new User();
        user.setActive(true);
        if (authUser instanceof NameIdentity) {
            final NameIdentity identity = (NameIdentity) authUser;
            final String name = identity.getName();
            if (name != null) {
                user.setName(name);
            }
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
