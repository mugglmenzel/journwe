package models.auth;

import models.GlobalParameters;
import models.UserManager;
import models.user.EUserRole;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

public class SecuredUser extends Security.Authenticator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.mvc.Security.Authenticator#getUsername(play.mvc.Http.Context)
	 */
	@Override
	public String getUsername(Context ctx) {
		final AuthUser u = PlayAuthenticate.getUser(ctx.session());

		if (u != null && isAuthorized(u)) {
			return u.getId();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * play.mvc.Security.Authenticator#onUnauthorized(play.mvc.Http.Context)
	 */
	@Override
	public Result onUnauthorized(Context ctx) {
		ctx.flash().put(GlobalParameters.FLASH_ERROR_KEY, "Nice try, but you need to log in first! Access is restricted to registered users only.");
		return redirect(controllers.core.html.routes.ApplicationController.index());
	}

    public static boolean isAuthorized(AuthUser u){
        return isUser(u) || isAdmin(u);
    }

    public static boolean isUser(AuthUser u) {
        return UserManager.findByAuthUserIdentity(u) != null && EUserRole.USER.equals(UserManager.findByAuthUserIdentity(u).getRole());

    }

	public static boolean isAdmin(AuthUser u) {
		return UserManager.findByAuthUserIdentity(u) != null && EUserRole.ADMIN.equals(UserManager.findByAuthUserIdentity(u).getRole());
	}

}
