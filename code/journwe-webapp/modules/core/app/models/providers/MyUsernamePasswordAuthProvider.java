package providers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.feth.play.module.mail.Mailer.Mail.Body;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.typesafe.config.ConfigFactory;
import models.GlobalParameters;
import models.UserManager;
import models.dao.user.TokenActionDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserEmailDAO;
import models.user.ETokenType;
import models.user.EUserRole;
import models.user.User;
import models.user.UserEmail;
import play.Application;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Http.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static play.data.Form.form;

public class MyUsernamePasswordAuthProvider
        extends
        UsernamePasswordAuthProvider<String, providers.MyLoginUsernamePasswordAuthUser, providers.MyUsernamePasswordAuthUser, MyUsernamePasswordAuthProvider.MyLogin, MyUsernamePasswordAuthProvider.MySignup> {

    public static final String PROVIDER_KEY = "password";

    private static final String SETTING_KEY_VERIFICATION_LINK_SECURE = SETTING_KEY_MAIL
            + "." + "verificationLink.secure";
    private static final String SETTING_KEY_PASSWORD_RESET_LINK_SECURE = SETTING_KEY_MAIL
            + "." + "passwordResetLink.secure";
    private static final String SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET = "loginAfterPasswordReset";

    private static final String EMAIL_TEMPLATE_FALLBACK_LANGUAGE = "en";

    @Override
    protected List<String> neededSettingKeys() {
        final List<String> needed = new ArrayList<String>(
                super.neededSettingKeys());
        needed.add(SETTING_KEY_VERIFICATION_LINK_SECURE);
        needed.add(SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
        needed.add(SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
        return needed;
    }

    public static MyUsernamePasswordAuthProvider getProvider() {
        return (MyUsernamePasswordAuthProvider) PlayAuthenticate
                .getProvider(UsernamePasswordAuthProvider.PROVIDER_KEY);
    }

    public static class MyIdentity {

        public MyIdentity() {
        }

        public MyIdentity(final String email) {
            this.email = email;
        }

        public String userId;

        @Required
        @Email
        public String email;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class MyLogin extends MyIdentity
            implements
            com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.UsernamePassword {

        @Required
        @MinLength(5)
        public String password;

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getPassword() {
            return password;
        }
    }

    public static class MySignup extends MyLogin {

        @Required
        @MinLength(5)
        public String repeatPassword;

        @Required
        public String name;

        public String validate() {
            if (password == null || !password.equals(repeatPassword)) {
                return Messages
                        .get("playauthenticate.password.signup.error.passwords_not_same");
            }
            return null;
        }
    }

    public static final Form<MySignup> SIGNUP_FORM = form(MySignup.class);
    public static final Form<MyLogin> LOGIN_FORM = form(MyLogin.class);

    public MyUsernamePasswordAuthProvider(Application app) {
        super(app);
    }

    protected Form<MySignup> getSignupForm() {
        return SIGNUP_FORM;
    }

    protected Form<MyLogin> getLoginForm() {
        return LOGIN_FORM;
    }

    @Override
    protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.SignupResult signupUser(final providers.MyUsernamePasswordAuthUser user) {
        // Finds a user if emailuser with this email exists.
        final User u = UserManager.findByUsernamePasswordIdentity(user);
        // A user with this email has been found.
        if (u != null) {
            // Get the primary email (as he might have multiple email addresses).
            final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(u.getId());
            if (ue != null) {
                // Wanted to sign up a user that already exists?
                // Try the Forgot Password? Link.
                if (ue.isValidated()) {
                    Logger.debug("SignupResult.USER_EXISTS");
                    // This user exists, has its email validated and is active
                    return SignupResult.USER_EXISTS;
                } else {
                    Logger.debug("SignupResult.USER_EXISTS_UNVERIFIED");
                    // this user exists, is active but has not yet validated its
                    // email.
                    // One reason ist, that the user has created an account but never
                    // verified his email.
                    // Another reason for this to happen can be that a user tries to register who has already
                    // signed up with a Facebook account (or other social media) that has this
                    // email. Facebook (and other social users) have "validated == 0" when they
                    // sign up. We need to validate their email before we can allow them to use this email.
//                    if(user.getId()==null) {
//                        // Create a password-user with the id of the facebook user.
//                        String userId = u.getId();
//                        UserSocial us = new UserSocial();
//                        us.setProvider("password");
//                        us.setSocialId(userId);
//                        us.setUserId(userId);
//                        new UserSocialDAO().save(us);
//                        // Save user's hashed password
//                        u.setHashedPassword(user.getHashedPassword());
//                        new UserDAO().save(u);
//
//                        // Problem with the following code:
//                        // the user captures the account for which he claimed
//                        // the email address
//                        // SECURITY PROBLEM !!!
//                        //user.setUserId(userId);
//                        //PlayAuthenticate.storeUser(Http.Context.current().session(),user);
//                    }
//                    return SignupResult.USER_EXISTS_UNVERIFIED;
                }
            }
        }
        // The user either does not exist or is inactive - create a new one.
        // Using this method also creates the password-UserSocial.
        final User newUser = UserManager.create(user, EUserRole.USER);
        // save the password-hash
        newUser.setHashedPassword(user.getHashedPassword());
        new UserDAO().save(newUser);
        user.setUserId(newUser.getId());
        // Update the session
        PlayAuthenticate.storeUser(Http.Context.current().session(),user);

        // Usually the email should be verified before allowing login, however
        // if you return
        // return SignupResult.USER_CREATED;
        // then the user gets logged in directly
        return SignupResult.USER_CREATED_UNVERIFIED;
    }

    @Override
    protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.LoginResult loginUser(
            final providers.MyLoginUsernamePasswordAuthUser authUser) {
        final User u = UserManager.findByUsernamePasswordIdentity(authUser);
        if (u == null) {
            return LoginResult.NOT_FOUND;
        } else {
            final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(u.getId());
            Logger.debug("MyUsernamePasswordAuthProvider: Login user with id " + u.getId());
            authUser.setUserId(u.getId());
            if (ue == null) {
                Logger.error("User " + u.getId() + " is not null, but he/she has no email. So he/she could not log in.");
                return LoginResult.USER_UNVERIFIED;
            }
            if (!ue.isValidated()) {
                Logger.debug("ue.isValidated(): " + ue.isValidated());
                Logger.debug("User and E-Mail has not been verified, yet: " + ue.getEmail() + " with user id " + u.getId());
                return LoginResult.USER_UNVERIFIED;
            } else {
                if (authUser.checkPassword(u.getHashedPassword(), authUser.getPassword())) {
                    // Password was correct
                    return LoginResult.USER_LOGGED_IN;
                } else {
                    return LoginResult.WRONG_PASSWORD;
                }
            }
        }
    }

    @Override
    protected Call userExists(final UsernamePasswordAuthUser authUser) {
        return controllers.core.html.routes.SignupController.exists();
    }

    @Override
    protected Call userUnverified(final UsernamePasswordAuthUser authUser) {
        return controllers.core.html.routes.SignupController.unverified();
    }

    @Override
    protected providers.MyUsernamePasswordAuthUser buildSignupAuthUser(
            final MySignup signup, final Context ctx) {
        // TODO
        providers.MyUsernamePasswordAuthUser toReturn = new providers.MyUsernamePasswordAuthUser(signup);
        Logger.debug("MyUsernamePasswordAuthUser buildSignupAuthUser() -> MyUsernamePasswordAuthUser.id = " + toReturn.getId());
        return toReturn;
    }

    @Override
    protected MyLoginUsernamePasswordAuthUser buildLoginAuthUser(
            final MyLogin login, final Context ctx) {
        // TODO
        MyLoginUsernamePasswordAuthUser toReturn = new MyLoginUsernamePasswordAuthUser(login.getPassword(),
                login.getEmail(), login.getUserId());
        Logger.debug("MyLoginUsernamePasswordAuthUser buildLoginAuthUser() -> MyLoginUsernamePasswordAuthUser.id = " + toReturn.getId());
        return toReturn;
    }


    @Override
    protected MyLoginUsernamePasswordAuthUser transformAuthUser(final MyUsernamePasswordAuthUser authUser, final Context context) {
        return new MyLoginUsernamePasswordAuthUser(authUser.getEmail(), authUser.getId());
    }

    @Override
    protected String getVerifyEmailMailingSubject(
            final providers.MyUsernamePasswordAuthUser user, final Context ctx) {
        return Messages.get("playauthenticate.password.verify_signup.subject");
    }

    @Override
    protected String onLoginUserNotFound(final Context context) {
        context.flash()
                .put(GlobalParameters.FLASH_ERROR_KEY,
                        Messages.get("playauthenticate.password.login.unknown_user_or_pw"));
        return super.onLoginUserNotFound(context);
    }

    @Override
    protected Body getVerifyEmailMailingBody(final String token,
                                             final providers.MyUsernamePasswordAuthUser user, final Context ctx) {

        final boolean isSecure = getConfiguration().getBoolean(
                SETTING_KEY_VERIFICATION_LINK_SECURE);
        final String url = controllers.core.html.routes.SignupController.verify(token).absoluteURL(
                ctx.request(), isSecure);

        final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
        final String langCode = lang.code();
        final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(user.getId());

        final String html = getEmailTemplate(
                "views.html.account.signup.email.verify_email", langCode, url,
                token, user.getName(), ue.getEmail());
        final String text = getEmailTemplate(
                "views.txt.account.signup.email.verify_email", langCode, url,
                token, user.getName(), ue.getEmail());

        return new Body(text, html);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected String generateVerificationRecord(
            final MyUsernamePasswordAuthUser user) {
        User u = UserManager.findByAuthUserIdentity(user);
        return generateVerificationRecord(u);
    }

    protected String generateVerificationRecord(final User user) {
        final String token = generateToken();
        // Do database actions, etc.
        new TokenActionDAO().create(ETokenType.EMAIL_VERIFICATION, token, user);
        return token;
    }

    protected String generatePasswordResetRecord(final User u) {
        final String token = generateToken();
        new TokenActionDAO().create(ETokenType.PASSWORD_RESET, token, u);
        return token;
    }

    protected String getPasswordResetMailingSubject(final User user,
                                                    final Context ctx) {
        return Messages.get("playauthenticate.password.reset_email.subject");
    }

    protected Body getPasswordResetMailingBody(final String token,
                                               final User user, final Context ctx) {

        final boolean isSecure = getConfiguration().getBoolean(
                SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
        final String url = controllers.core.html.routes.SignupController.resetPassword(token).absoluteURL(
                ctx.request(), isSecure);

        final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
        final String langCode = lang.code();
        final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(user.getId());

        final String html = getEmailTemplate(
                "views.html.account.email.password_reset", langCode, url,
                token, user.getName(), ue.getEmail());
        final String text = getEmailTemplate(
                "views.txt.account.email.password_reset", langCode, url, token,
                user.getName(), ue.getEmail());

        return new Body(text, html);
    }

    public void sendPasswordResetMailing(final User user, final Context ctx) {
        final String token = generatePasswordResetRecord(user);
        final String subject = getPasswordResetMailingSubject(user, ctx);
        final Body body = getPasswordResetMailingBody(token, user, ctx);
        sendSESMail(subject, body, getEmailName(user));
    }

    public boolean isLoginAfterPasswordReset() {
        return getConfiguration().getBoolean(
                SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
    }

    protected String getVerifyEmailMailingSubjectAfterSignup(final User user,
                                                             final Context ctx) {
        return Messages.get("playauthenticate.password.verify_email.subject");
    }

    protected String getEmailTemplate(final String template,
                                      final String langCode, final String url, final String token,
                                      final String name, final String email) {
        Class<?> cls = null;
        String ret = null;
        try {
            cls = Class.forName(template + "_" + langCode);
        } catch (ClassNotFoundException e) {
            Logger.warn("Template: '"
                    + template
                    + "_"
                    + langCode
                    + "' was not found! Trying to use English fallback template instead.");
        }
        if (cls == null) {
            try {
                cls = Class.forName(template + "_"
                        + EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
            } catch (ClassNotFoundException e) {
                Logger.error("Fallback template: '" + template + "_"
                        + EMAIL_TEMPLATE_FALLBACK_LANGUAGE
                        + "' was not found either!");
            }
        }
        if (cls != null) {
            Method htmlRender = null;
            try {
                htmlRender = cls.getMethod("render", String.class,
                        String.class, String.class, String.class);
                ret = htmlRender.invoke(null, url, token, name, email)
                        .toString();

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    protected Body getVerifyEmailMailingBodyAfterSignup(final String token,
                                                        final User user, final Context ctx) {

        final boolean isSecure = getConfiguration().getBoolean(
                SETTING_KEY_VERIFICATION_LINK_SECURE);
        final String url = controllers.core.html.routes.SignupController.verify(token).absoluteURL(
                ctx.request(), isSecure);

        final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
        final String langCode = lang.code();
        final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(user.getId());

        final String html = getEmailTemplate(
                "views.html.account.email.verify_email", langCode, url, token,
                user.getName(), ue.getEmail());
        final String text = getEmailTemplate(
                "views.txt.account.email.verify_email", langCode, url, token,
                user.getName(), ue.getEmail());

        return new Body(text, html);
    }

    public void sendVerifyEmailMailingAfterSignup(final User user,
                                                  final Context ctx) {
        Logger.debug("sendVerifyEmailMailingAfterSignup for user "+user);
        if(user!=null)
            Logger.debug("with userid "+user.getId());
//        if (user != null) {
        final String subject = getVerifyEmailMailingSubjectAfterSignup(user,
                    ctx);
        final String token = generateVerificationRecord(user);
        final Body body = getVerifyEmailMailingBodyAfterSignup(token, user, ctx);
            sendSESMail(subject, body, getEmailName(user));
//        } else {
//            Logger.warn("Aborted sending verification-email mailing because supplied user is null.");
//        }
    }

    private String getEmailName(final User user) {
        final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(user.getId());
        return getEmailName(ue.getEmail(), user.getName());
    }

    /**
     * Helper.
     */
    private void sendSESMail(String subject, Body body, String email) {
        AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                ConfigFactory.load().getString("aws.accessKey"),
                ConfigFactory.load().getString("aws.secretKey")));
        ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(email)).withMessage(new Message().withSubject(new Content().withData(subject)).withBody(new com.amazonaws.services.simpleemail.model.Body().withText(new Content().withData(body.getText())))).withSource("info@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
    }
}
