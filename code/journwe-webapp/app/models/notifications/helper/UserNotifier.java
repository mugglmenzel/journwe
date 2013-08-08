package models.notifications.helper;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.typesafe.config.ConfigFactory;
import models.dao.NotificationDAO;
import models.dao.NotificationDigestQueueDAO;
import models.dao.UserDAO;
import models.dao.UserEmailDAO;
import models.notifications.ENotificationFrequency;
import models.notifications.ENotificationTopics;
import models.notifications.Notification;
import models.user.User;
import models.user.UserEmail;
import play.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 30.07.13
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */
public class UserNotifier {


    public void notifyUser(String userId, String message) {
        this.notifyUser(userId, ENotificationTopics.GENERAL, message);
    }


    public void notifyUser(String userId, ENotificationTopics topic, String message) {
        this.notifyUser(userId, topic, message, null);
    }


    public void notifyUser(String userId, ENotificationTopics topic, String message, String subject) {
        this.notifyUser(new UserDAO().get(userId), topic, message, subject);
    }

    public void notifyUser(User user, String message) {
        this.notifyUser(user, ENotificationTopics.GENERAL, message);
    }

    public void notifyUser(User user, ENotificationTopics topic, String message) {
        this.notifyUser(user, topic, message, null);
    }

    public void notifyUser(User user, ENotificationTopics topic, String message, String subject) {

        Notification noti = new Notification();
        noti.setUserId(user.getId());
        noti.setTopic(topic);
        noti.setMessage(message);
        noti.setSubject(subject);
        new NotificationDAO().save(noti);

        if (ENotificationFrequency.IMMEDIATELY.equals(user.getNotificationDigest()))
            notifyUserViaEmail(noti);

    }


    public void notifyUsersDigest(ENotificationFrequency frequency, int minimumAge) {
        Logger.debug("Checking for " + frequency.getDigestName() + " with " + new NotificationDigestQueueDAO().getDigestUsers(frequency).size() + " users, minimumAge: " + minimumAge);
        for (User user : new NotificationDigestQueueDAO().getDigestUsers(frequency)) {
            Logger.debug("user " + user.getName() + " wants a digest? " + (new Date().getTime() > user.getLastDigest().getTime() + minimumAge) + "(lastDigest: " + user.getLastDigest().getTime() + ", now: " + new Date().getTime() + ")");
            if (new Date().getTime() > user.getLastDigest().getTime() + minimumAge) {
                Logger.debug("checking notifications for " + user.getName());
                new UserNotifier().notifyUserViaEmailDigest(new NotificationDAO().unsent(user.getId()), frequency.getDigestName());
                user.setLastDigest(new Date());
                new UserDAO().save(user);

                new NotificationDigestQueueDAO().delete(frequency.getDigestName(), user.getId());
            }
        }
    }



    /* Email Stuff */

    public void notifyUserViaEmail(Notification notification) {
        sendNotificationEmail(notification.getUserId(), "JournWe Notification" + (notification.getSubject() != null && !"".equals(notification.getSubject()) ? ": " + notification.getSubject() : ""), notification.getMessage());
        markNotificationSent(notification);
    }

    public void notifyUserViaEmailDigest(List<Notification> notifications, String digestName) {
        if (notifications.size() > 0) {

            String message = "You have following Notifications: \n";
            for (Notification notification : notifications) {
                message += "+ " + notification.getSubject() + ": " + notification.getMessage() + "\n";
                markNotificationSent(notification);
            }

            sendNotificationEmail(notifications.get(0).getUserId(), "JournWe Notification: " + digestName + " Digest", message);
        }

    }

    private void markNotificationSent(Notification notification) {
        notification.setSent(true);
        new NotificationDAO().save(notification);
    }


    private void sendNotificationEmail(String userId, String subject, String message) {
        try {
            UserEmail primaryEmail = new UserEmailDAO().getPrimaryEmailOfUser(userId);
            if (primaryEmail != null) {
                AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                Logger.debug("got primary email: " + primaryEmail);
                ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(primaryEmail.getEmail())).withMessage(new Message().withSubject(new Content().withData(subject)).withBody(new Body().withText(new Content().withData(message)))).withSource("no-reply@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
