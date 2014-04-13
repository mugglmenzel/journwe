package services;

import models.dao.adventure.AdventureDAO;
import models.dao.user.UserDAO;
import models.notifications.ENotificationTopics;

/**
 * Created by mugglmenzel on 13/04/14.
 */
public class NotificationObjectResolver {


    public static Object get(ENotificationTopics topic, String topicRef) {
        switch(topic) {
            case ADVENTURE:
                return topicRef != null ? new AdventureDAO().get(topicRef) : null;
            case USER:
                return topicRef != null ? new UserDAO().get(topicRef) : null;
            case GENERAL:
                return null;
        }
        return null;
    }
}
