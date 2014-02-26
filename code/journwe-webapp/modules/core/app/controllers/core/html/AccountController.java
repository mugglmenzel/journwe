package controllers.core.html;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.GlobalParameters;
import models.UserManager;
import models.auth.SecuredUser;
import models.dao.user.UserEmailDAO;
import models.user.User;
import models.user.UserEmail;
import play.Logger;
import play.data.Form;
import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;
import play.mvc.*;
import models.providers.MyUsernamePasswordAuthProvider;
import models.providers.MyUsernamePasswordAuthUser;
import views.html.account.link;

import static play.data.Form.form;

public class AccountController extends Controller {

    public static class Accept {

        @Required
        @NonEmpty
        public Boolean accept;

        public Boolean getAccept() {
            return accept;
        }

        public void setAccept(Boolean accept) {
            this.accept = accept;
        }

    }

    public static class PasswordChange {
        @MinLength(5)
        @Required
        public String password;

        @MinLength(5)
        @Required
        public String repeatPassword;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRepeatPassword() {
            return repeatPassword;
        }

        public void setRepeatPassword(String repeatPassword) {
            this.repeatPassword = repeatPassword;
        }

        public String validate() {
            if (password == null || !password.equals(repeatPassword)) {
                return Messages
                        .get("playauthenticate.change_password.error.passwords_not_same");
            }
            return null;
        }
    }

    private static final Form<Accept> ACCEPT_FORM = form(Accept.class);
    private static final Form<AccountController.PasswordChange> PASSWORD_CHANGE_FORM = form(AccountController.PasswordChange.class);

    public static Result link() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        return ok(link.render());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result verifyEmail() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        AuthUser auth = PlayAuthenticate.getUser(Http.Context.current());
        final User user = UserManager.findByAuthUserIdentity(auth);
        final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(user.getId());
        if (ue.isValidated()) {
            // E-Mail has been validated already
            flash(GlobalParameters.FLASH_MESSAGE_KEY,
                    Messages.get("playauthenticate.verify_email.error.already_validated"));
        } else if (ue != null && ue.getEmail() != null && !ue.getEmail().trim().isEmpty()) {
            flash(GlobalParameters.FLASH_MESSAGE_KEY, Messages.get(
                    "playauthenticate.verify_email.message.instructions_sent",
                    ue.getEmail()));
            MyUsernamePasswordAuthProvider.getProvider()
                    .sendVerifyEmailMailingAfterSignup(user, ctx());
        } else {
            flash(GlobalParameters.FLASH_MESSAGE_KEY, Messages.get(
                    "playauthenticate.verify_email.error.set_email_first",
                    ue.getEmail()));
        }
        // TODO
        return redirect(controllers.core.html.routes.ApplicationController.index());
        //return redirect(routes.ApplicationController.profile());
    }

    public static Result changePassword() {
        Logger.debug("changePassword");
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        AuthUser auth = PlayAuthenticate.getUser(Http.Context.current());
        final User user = UserManager.findByAuthUserIdentity(auth);
        final UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(user.getId());
        if (!ue.isValidated()) {
            return ok(views.html.account.unverified.render());
        } else {
            return ok(views.html.account.password_change.render(PASSWORD_CHANGE_FORM));
        }
    }

    public static Result doChangePassword() {
        Logger.debug("doChangePassword");
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<AccountController.PasswordChange> filledForm = PASSWORD_CHANGE_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not select whether to link or not link
            return badRequest(views.html.account.password_change.render(filledForm));
        } else {
            AuthUser auth = PlayAuthenticate.getUser(Http.Context.current());
            User user = UserManager.findByAuthUserIdentity(auth);
            UserEmail ue = new UserEmailDAO().getPrimaryEmailOfUser(user.getId());
            final String newPassword = filledForm.get().password;
            UserManager.changePassword(new MyUsernamePasswordAuthUser(user, newPassword, ue.getEmail()), newPassword, true);
            flash(GlobalParameters.FLASH_MESSAGE_KEY,
                    Messages.get("playauthenticate.change_password.success"));
            return redirect(controllers.core.html.routes.ApplicationController.index());
        }
    }

    public static Result askLink() {
        Logger.debug("askLink");
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final AuthUser u = PlayAuthenticate.getLinkUser(session());
        if (u == null) {
            // account to link could not be found, silently redirect to login
            return Results.redirect(controllers.core.html.routes.ApplicationController.index());
        }
        return ok(views.html.account.ask_link.render(ACCEPT_FORM, u));
    }

    public static Result doLink() {
        Logger.debug("doLink");
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final AuthUser u = PlayAuthenticate.getLinkUser(session());
        if (u == null) {
            Logger.debug("account to link could not be found. Silently redirect to login.");
            // account to link could not be found, silently redirect to login
            return redirect(controllers.core.html.routes.ApplicationController.index());
        }

        final Form<Accept> filledForm = ACCEPT_FORM.bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not select whether to link or not link
            return badRequest(views.html.account.ask_link.render(filledForm, u));
        } else {
            // User made a choice :)
            final boolean link = filledForm.get().accept;
            if (link) {
                flash(GlobalParameters.FLASH_MESSAGE_KEY,
                        Messages.get("playauthenticate.accounts.link.success"));
            }
            return PlayAuthenticate.link(ctx(), link);
        }
    }

    public static Result askMerge() {
        Logger.debug("askMerge");
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        // this is the currently logged in user
        final AuthUser aUser = PlayAuthenticate.getUser(session());
        // this is the user that was selected for a login
        final AuthUser bUser = PlayAuthenticate.getMergeUser(session());
        if (bUser == null) {
            Logger.debug("User to merge with could not be found. Silently redirect to login.");
            // user to merge with could not be found, silently redirect to login
            return redirect(controllers.core.html.routes.ApplicationController.index());
        }

        // You could also get the local user object here via
        // User.findByAuthUserIdentity(newUser)
        return ok(views.html.account.ask_merge.render(ACCEPT_FORM, aUser, bUser));
    }

    public static Result doMerge() {
        Logger.debug("doMerge");
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        // this is the currently logged in user
        final AuthUser aUser = PlayAuthenticate.getUser(session());

        // this is the user that was selected for a login
        final AuthUser bUser = PlayAuthenticate.getMergeUser(session());
        if (bUser == null) {
            // user to merge with could not be found, silently redirect to login
            return redirect(controllers.core.html.routes.ApplicationController.index());
        }

        final Form<Accept> filledForm = ACCEPT_FORM.bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not select whether to merge or not merge
            return badRequest(views.html.account.ask_merge.render(filledForm, aUser, bUser));
        } else {
            // User made a choice :)
            final boolean merge = filledForm.get().accept;
            if (merge) {
                flash(GlobalParameters.FLASH_MESSAGE_KEY,
                        Messages.get("playauthenticate.accounts.merge.success"));
            }
            return PlayAuthenticate.merge(ctx(), merge);
        }
    }

}
