package models.dao.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.dao.common.CommonEntityDAO;
import models.user.*;
import play.Logger;
import play.mvc.Controller;

import java.util.List;

public class UserDAO extends CommonEntityDAO<User> {

    public static final String COOKIE_USER_ROLE_ON_REGISTER = "play-authenticate-user-role-on-register";
    public static final EUserRole DEFAULT_USER_ROLE = EUserRole.USER;

    public UserDAO() {
        super(User.class);
    }

    public User findByProviderAndSocialId(String provider, String socialId) {
        UserSocial social = new UserSocialDAO().get(socialId, provider);
        return social != null ? new UserDAO().get(social.getUserId()) : null;
    }

    /**
     * Find the user by his email address.
     * <p/>
     * This method is for example necessary for password recovery.
     *
     * @param email
     * @return
     */
    public User findByEmail(final String email) {
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


    public EUserRole getRegisterUserRole() {
        Logger.debug("getting user role from cookie " + Controller.request().cookie(UserDAO.COOKIE_USER_ROLE_ON_REGISTER));
        return Controller.request().cookie(COOKIE_USER_ROLE_ON_REGISTER) != null ? EUserRole.valueOf(Controller.request().cookie(COOKIE_USER_ROLE_ON_REGISTER).value()) : DEFAULT_USER_ROLE;
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

    public void merge(final String oldProvider, final String oldSocialId, final String newProvider, final String newSocialId) {
        UserSocialDAO usdao = new UserSocialDAO();
        UserSocial us = usdao.get(oldProvider, oldSocialId);
        final User newUser = new UserDAO().findByProviderAndSocialId(newProvider, newSocialId);
        us.setUserId(newUser.getId());
        usdao.save(us);
        // do all other merging stuff here - like resources, etc.
        // TODO just throw away the adventures etc. of the other user?
        // deactivate the merged user that got added to this one
        final User oldUser = new UserDAO().findByProviderAndSocialId(oldProvider, oldSocialId);
        oldUser.setActive(false);
        new UserDAO().save(oldUser);
    }

    public void changePassword(final String provider, final String socialId, final String password,
                               final boolean create) {
        UserSocial us = new UserSocialDAO().findBySocialId(provider, socialId);
        if (us == null) {
            if (create) {
                us = new UserSocialDAO().create(provider, socialId);
            } else {
                throw new RuntimeException(
                        "account not enabled for password usage");
            }
        }
        User u = new UserDAO().findByProviderAndSocialId(provider, socialId);
        Logger.debug("Set hashed password: " + password);
        u.setHashedPassword(password);
        new UserDAO().save(u);
        us.setUserId(u.getId());
        new UserSocialDAO().save(us);
    }

    public void resetPassword(final String provider, final String socialId, final String password,
                              final boolean create) {
        // You might want to wrap this into a transaction
        this.changePassword(provider, socialId, password, create);
        User user = new UserDAO().findByProviderAndSocialId(provider, socialId);
        new TokenActionDAO().deleteByUser(user, ETokenType.PASSWORD_RESET);
    }


}
