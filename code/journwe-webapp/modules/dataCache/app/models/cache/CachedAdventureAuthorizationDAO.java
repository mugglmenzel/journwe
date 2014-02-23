package models.cache;

import models.adventure.AdventureAuthorization;
import models.dao.AdventureAuthorizationDAO;
import play.Logger;
import play.cache.Cache;

import java.util.concurrent.Callable;

/**
 * Created by mugglmenzel on 23.02.14.
 */
public class CachedAdventureAuthorizationDAO extends AdventureAuthorizationDAO {

    @Override
    public AdventureAuthorization get(final String advId, final String usrId) {
        try {
        return Cache.getOrElse("adventureAuthorization." + advId + "." + usrId, new Callable<AdventureAuthorization>() {
            @Override
            public AdventureAuthorization call() throws Exception {
                Logger.debug("fetched authorization from dynamodb");
                return new AdventureAuthorizationDAO().get(advId, usrId);
            }
        }, 24 * 3600);
        } catch (Exception e) {
            return super.get(advId, usrId);
        }
    }
}
