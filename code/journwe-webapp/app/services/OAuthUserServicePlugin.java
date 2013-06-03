package services;

import models.User;
import models.UserSocial;
import models.dao.UserSocialDAO;
import play.Application;
import play.Logger;

import com.feth.play.module.pa.controllers.Authenticate;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.exceptions.RedirectUriMismatch;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider.Constants;
import com.feth.play.module.pa.service.UserServicePlugin;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;

public class OAuthUserServicePlugin extends UserServicePlugin {

	public OAuthUserServicePlugin(final Application app) {
		super(app);
	}

	@Override
	public Object save(final AuthUser authUser) {
		final boolean isLinked = User.existsByAuthUserIdentity(authUser);
		if (!isLinked) {
			return User.create(authUser).getId();
		} else {
			// we have this user already, so return null
			return null;
		}
	}

	@Override
	public Object getLocalIdentity(final AuthUserIdentity identity) {
		// For production: Caching might be a good idea here...
		// ...and dont forget to sync the cache when users get deactivated/deleted
		final User u = User.findByAuthUserIdentity(identity);
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
