package models.notifications;

import play.i18n.Messages;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 31.07.13
 * Time: 00:10
 * To change this template use File | Settings | File Templates.
 */
public enum ENotificationFrequency {

    NONE, IMMEDIATELY, DAILY, WEEKLY;

    public String getDigestName() {
        return Messages.get("notifications.digest.name." + toString());
    }


}
