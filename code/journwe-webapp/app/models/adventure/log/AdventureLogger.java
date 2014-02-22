package models.adventure.log;

import com.feth.play.module.pa.PlayAuthenticate;
import models.UserManager;
import models.dao.adventure.AdventureLogDAO;
import models.dao.user.UserDAO;
import models.user.User;
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
        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        if(usr != null)
            new AdventureLogDAO().save(new AdventureLogEntry(advId, usr.getId(), new Date().getTime(), type, topic, section, data));
    }

}
