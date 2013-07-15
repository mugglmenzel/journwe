package models.auth;

import models.dao.UserDAO;
import models.user.EUserRole;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import controllers.routes;

public class SecuredBetaUser extends Security.Authenticator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.mvc.Security.Authenticator#getUsername(play.mvc.Http.Context)
	 */
	@Override
	public String getUsername(Context ctx) {
		final AuthUser u = PlayAuthenticate.getUser(ctx.session());

		if (u != null && (isBeta(u) || isAdmin(u))) {
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
		ctx.flash().put("error", "Nice try, but you need to log in first! Access is restricted to selected BETA users only.");
		return redirect(routes.ApplicationController.index());
	}


    public static boolean isBeta(AuthUser u) {
        return new UserDAO().findByAuthUserIdentity(u) != null && EUserRole.BETA.equals(new UserDAO().findByAuthUserIdentity(u).getRole());
    }

	public static boolean isAdmin(AuthUser u) {
		return new UserDAO().findByAuthUserIdentity(u) != null && EUserRole.ADMIN.equals(new UserDAO().findByAuthUserIdentity(u).getRole());
	}

}
