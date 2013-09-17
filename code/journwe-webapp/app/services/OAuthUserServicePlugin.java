package services;

import com.feth.play.module.pa.service.UserServicePlugin;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import models.dao.UserDAO;
import models.user.EUserRole;
import models.user.User;
import play.Application;
import play.Logger;
import play.mvc.Controller;

public class OAuthUserServicePlugin extends UserServicePlugin {


    public OAuthUserServicePlugin(final Application app) {
        super(app);
    }

    @Override
    public Object save(final AuthUser authUser) {
        Logger.debug("saving auth identity");

        final boolean isLinked = new UserDAO().existsByAuthUserIdentity(authUser);
        if (!isLinked) {
            return new UserDAO().create(authUser, new UserDAO().getRegisterUserRole()).getId();
        } else {
            new UserDAO().update(authUser, authUser);
            // we have this user already, so return null
            return null;
        }
    }

    @Override
    public AuthUser update(AuthUser knownUser) {
        Logger.debug("updating user");
        new UserDAO().update(knownUser, knownUser);
        return knownUser;
    }

    @Override
    public Object getLocalIdentity(final AuthUserIdentity identity) {
        Logger.debug("getting local auth identity (id)");
        // For production: Caching might be a good idea here...
        // ...and dont forget to sync the cache when users get deactivated/deleted
        final User u = new UserDAO().findByAuthUserIdentity(identity);

        return u != null ? u.getId() : null;

    }

    @Override
    public AuthUser merge(final AuthUser newUser, final AuthUser oldUser) {
        Logger.debug("merging auth users");
        new UserDAO().update(newUser, oldUser);
        return newUser;
    }

    @Override
    public AuthUser link(final AuthUser oldUser, final AuthUser newUser) {
        Logger.debug("linking accounts");
        // TODO: IMPLEMENT THIS User.addLinkedAccount(oldUser, newUser);
        return null;
    }

}
