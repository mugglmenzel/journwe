package models.auth;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import controllers.routes;
import models.dao.UserDAO;
import models.user.EUserRole;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SecuredAdminUser extends Security.Authenticator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.mvc.Security.Authenticator#getUsername(play.mvc.Http.Context)
	 */
	@Override
	public String getUsername(Context ctx) {
		final AuthUser u = PlayAuthenticate.getUser(ctx.session());

		if (u != null && isAdmin(u)) {
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
		ctx.flash().put("error", "Nice try, but you need to be Admin!");
		return redirect(routes.ApplicationController.index());
	}

    public static boolean isAuthorized(AuthUser u){
        return isAdmin(u);
    }

	public static boolean isAdmin(AuthUser u) {
		return new UserDAO().findByAuthUserIdentity(u) != null && EUserRole.ADMIN.equals(new UserDAO().findByAuthUserIdentity(u).getRole());
	}

}
