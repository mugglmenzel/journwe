package controllers;

import com.feth.play.module.pa.user.AuthUser;
import models.dao.TokenActionDAO;
import models.dao.UserDAO;
import models.dao.UserEmailDAO;
import models.user.TokenAction;
import models.user.ETokenType;
import models.user.User;
import models.user.UserEmail;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import providers.MyLoginUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyIdentity;
import providers.MyUsernamePasswordAuthUser;
import views.html.account.signup.*;

import com.feth.play.module.pa.PlayAuthenticate;
import views.html.account.signup.exists$;

import static play.data.Form.form;

public class Signup extends Controller {

	public static class PasswordReset extends Account.PasswordChange {

		public PasswordReset() {
		}

		public PasswordReset(final String token) {
			this.token = token;
		}

		public String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	private static final Form<PasswordReset> PASSWORD_RESET_FORM = form(PasswordReset.class);

	public static Result unverified() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(views.html.account.signup.unverified.render());
	}

	private static final Form<MyIdentity> FORGOT_PASSWORD_FORM = form(MyIdentity.class);

	public static Result forgotPassword(final String email) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		Form<MyIdentity> form = FORGOT_PASSWORD_FORM;
		if (email != null && !email.trim().isEmpty()) {
			form = FORGOT_PASSWORD_FORM.fill(new MyIdentity(email));
		}
		return ok(views.html.account.signup.password_forgot.render(form));
	}

	public static Result doForgotPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyIdentity> filledForm = FORGOT_PASSWORD_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill in his/her email
			return badRequest(views.html.account.signup.password_forgot.render(filledForm));
		} else {
			// The email address given *BY AN UNKNWON PERSON* to the form - we
			// should find out if we actually have a user with this email
			// address and whether password login is enabled for him/her. Also
			// only send if the email address of the user has been verified.
			final String email = filledForm.get().email;

			// We don't want to expose whether a given email address is signed
			// up, so just say an email has been sent, even though it might not
			// be true - that's protecting our user privacy.
			flash(ApplicationController.FLASH_MESSAGE_KEY,
					Messages.get(
							"playauthenticate.reset_password.message.instructions_sent",
							email));

			final User user = new UserDAO().findByEmail(email);
			if (user != null) {
				// yep, we have a user with this email that is active - we do
				// not know if the user owning that account has requested this
				// reset, though.
				final MyUsernamePasswordAuthProvider provider = MyUsernamePasswordAuthProvider
						.getProvider();
                final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(user.getId());
				// User exists
				if (ue.isValidated()) {
					provider.sendPasswordResetMailing(user, ctx());
					// In case you actually want to let (the unknown person)
					// know whether a user was found/an email was sent, use,
					// change the flash message
				} else {
					// We need to change the message here, otherwise the user
					// does not understand whats going on - we should not verify
					// with the password reset, as a "bad" user could then sign
					// up with a fake email via OAuth and get it verified by an
					// a unsuspecting user that clicks the link.
					flash(ApplicationController.FLASH_MESSAGE_KEY,
							Messages.get("playauthenticate.reset_password.message.email_not_verified"));

					// You might want to re-send the verification email here...
					provider.sendVerifyEmailMailingAfterSignup(user, ctx());
				}
			}

			return redirect(routes.ApplicationController.index());
		}
	}

	/**
	 * Returns a token object if valid, null if not
	 * 
	 * @param token
	 * @param type
	 * @return
	 */
	private static TokenAction tokenIsValid(final String token, final ETokenType type) {
		TokenAction ret = null;
        if (token != null && !token.trim().isEmpty()) {
			final TokenAction ta = new TokenActionDAO().get(type, token);
			if (ta != null && ta.isValid()) {
				ret = ta;
			}
		}

		return ret;
	}

	public static Result resetPassword(final String token) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, ETokenType.PASSWORD_RESET);
		if (ta == null) {
			return badRequest(views.html.account.signup.no_token_or_invalid.render());
		}

		return ok(views.html.account.signup.password_reset.render(PASSWORD_RESET_FORM
                .fill(new PasswordReset(token))));
	}

	public static Result doResetPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<PasswordReset> filledForm = PASSWORD_RESET_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.account.signup.password_reset.render(filledForm));
		} else {
			final String token = filledForm.get().token;
			final String newPassword = filledForm.get().password;

			final TokenAction ta = tokenIsValid(token, ETokenType.PASSWORD_RESET);
			if (ta == null) {
				return badRequest(views.html.account.signup.no_token_or_invalid.render());
			}
            final User u = new UserDAO().get(ta.getTargetUserId());
			try {
				// Pass true for the second parameter if you want to
				// automatically create a password and the exception never to
				// happen
				new UserDAO().resetPassword(new MyUsernamePasswordAuthUser(u,newPassword,null),
						false);
			} catch (final RuntimeException re) {
				flash(ApplicationController.FLASH_MESSAGE_KEY,
						Messages.get("playauthenticate.reset_password.message.no_password_account"));
			}
			final boolean login = MyUsernamePasswordAuthProvider.getProvider()
					.isLoginAfterPasswordReset();
			if (login) {
				// automatically log in
				flash(ApplicationController.FLASH_MESSAGE_KEY,
						Messages.get("playauthenticate.reset_password.message.success.auto_login"));
                final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(u.getId());
				return PlayAuthenticate.loginAndRedirect(ctx(),
						new MyLoginUsernamePasswordAuthUser(ue.getEmail(),ue.getUserId()));
			} else {
				// send the user to the login page
				flash(ApplicationController.FLASH_MESSAGE_KEY,
						Messages.get("playauthenticate.reset_password.message.success.manual_login"));
			}
			return redirect(routes.ApplicationController.login());
		}
	}

	public static Result oAuthDenied(final String getProviderKey) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(views.html.account.signup.oAuthDenied.render(getProviderKey));
	}

	public static Result exists() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(exists.render());
	}

	public static Result verify(final String token) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, ETokenType.EMAIL_VERIFICATION);
		if (ta == null) {
			return badRequest(views.html.account.signup.no_token_or_invalid.render());
		}
        final User tauser = new UserDAO().get(ta.getTargetUserId());
        final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(tauser.getId());
		final String email = ue.getEmail();
		new UserDAO().verify(tauser);
		flash(ApplicationController.FLASH_MESSAGE_KEY,
				Messages.get("playauthenticate.verify_email.success", email));
        AuthUser auth = PlayAuthenticate.getUser(Http.Context.current());
        if(auth == null)
            return redirect(routes.ApplicationController.login());
        final User user = new UserDAO().findByAuthUserIdentity(auth);
        if(user == null)
            return redirect(routes.ApplicationController.login());
		return redirect(routes.ApplicationController.index());
	}
}
