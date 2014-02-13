package models.adventure.log;

import models.dao.adventure.AdventureLogDAO;

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
        new AdventureLogDAO().save(new AdventureLogEntry(advId, new Date().getTime(), type, topic, section, data));
    }

}
