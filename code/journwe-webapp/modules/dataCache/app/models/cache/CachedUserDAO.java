package models.cache;

import models.user.User;
import models.dao.user.UserDAO;
import models.user.UserSocial;
import models.dao.user.UserSocialDAO;
import play.Logger;
import play.cache.Cache;

import java.util.concurrent.Callable;

/**
 * Created by mugglmenzel on 21/02/14.
 */
public class CachedUserDAO extends UserDAO {


    public User getUserBySocial(final String provider, final String socialId) {
        User result = null;
        try {
            result = Cache.getOrElse("user.social." + provider + "." + socialId, new Callable<User>() {
                @Override
                public User call() throws Exception {
                    return new UserDAO().findByProviderAndSocialId(provider, socialId);
                }
            }, 3600);
        } catch (Exception e) {
            Logger.error("Error while looking up user. Ouch!", e);
        }

        return result;
    }

    public void clearCache(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (UserSocial identity : new UserSocialDAO().findByUserId(userId))
                    Cache.remove("user.social." + identity.getProvider() + "." + identity.getSocialId());
                Cache.remove("user." + userId + ".myadventures");
            }
        }).start();
    }
}
