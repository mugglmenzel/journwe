package services;

import models.dao.UserDAO;
import models.user.User;
import play.Application;

import com.feth.play.module.pa.service.UserServicePlugin;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;

public class OAuthUserServicePlugin extends UserServicePlugin {

	public OAuthUserServicePlugin(final Application app) {
		super(app);
	}

	@Override
	public Object save(final AuthUser authUser) {
		final boolean isLinked = new UserDAO().existsByAuthUserIdentity(authUser);
		if (!isLinked) {
			return new UserDAO().create(authUser).getId();
		} else {
			// we have this user already, so return null
			return null;
		}
	}

	@Override
	public Object getLocalIdentity(final AuthUserIdentity identity) {
		// For production: Caching might be a good idea here...
		// ...and dont forget to sync the cache when users get deactivated/deleted
		final User u = new UserDAO().findByAuthUserIdentity(identity);
		if(u != null) {
			return u.getId();
		} else {
			return null;
		}
	}

	@Override
	public AuthUser merge(final AuthUser newUser, final AuthUser oldUser) {
		// TODO: IMPLEMENT THIS
		return oldUser;
	}

	@Override
	public AuthUser link(final AuthUser oldUser, final AuthUser newUser) {
		// TODO: IMPLEMENT THIS User.addLinkedAccount(oldUser, newUser);
		return null;
	}

}
