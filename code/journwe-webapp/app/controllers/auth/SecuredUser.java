package controllers.auth;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import controllers.routes;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SecuredUser extends Security.Authenticator {

    /*
     * (non-Javadoc)
     *
     * @see play.mvc.Security.Authenticator#getUsername(play.mvc.Http.Context)
     */
    @Override
    public String getUsername(Context ctx) {
        final AuthUser u = PlayAuthenticate.getUser(ctx.session());

        if (u != null) {
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
        ctx.flash().put("error", "Nice try, but you need to log in first!");
        return redirect(routes.ApplicationController.index());
    }

}
