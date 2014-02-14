package models.adventure.log;

import com.feth.play.module.pa.PlayAuthenticate;
import models.dao.adventure.AdventureLogDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserSocialDAO;
import models.user.User;
import models.user.UserSocial;
import play.mvc.Http;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 13.02.14
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */
public class AdventureLogger {

    public static void log(String advId, EAdventureLogType type, EAdventureLogTopic topic, EAdventureLogSection section, String data) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        if(usr != null)
            new AdventureLogDAO().save(new AdventureLogEntry(advId, usr.getId(), new Date().getTime(), type, topic, section, data));
    }

}
