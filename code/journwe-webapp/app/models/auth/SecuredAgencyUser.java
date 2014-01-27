package models.auth;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.dao.user.UserDAO;
import models.user.EUserRole;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SecuredAgencyUser extends Security.Authenticator {

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
		ctx.flash().put("error", "Nice try, but you need to log in first! Access is restricted to travel agencies only.");
		return redirect(controllers.html.routes.ApplicationController.index());
	}

    public static boolean isAuthorized(AuthUser u){
        return isUser(u) || isAdmin(u);
    }

    public static boolean isUser(AuthUser u) {
        return new UserDAO().findByAuthUserIdentity(u) != null && EUserRole.AGENCY.equals(new UserDAO().findByAuthUserIdentity(u).getRole());

    }

	public static boolean isAdmin(AuthUser u) {
		return new UserDAO().findByAuthUserIdentity(u) != null && EUserRole.ADMIN.equals(new UserDAO().findByAuthUserIdentity(u).getRole());
	}

}
