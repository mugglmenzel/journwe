package models.notifications;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.typesafe.config.ConfigFactory;
import models.dao.NotificationDAO;
import models.dao.UserDAO;
import models.dao.UserEmailDAO;
import models.user.User;
import models.user.UserEmail;
import play.Logger;

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

    public void notifyUserViaEmail(Notification notification) {
        try {
            UserEmail primaryEmail = new UserEmailDAO().getPrimaryEmailOfUser(notification.getUserId());
            if (primaryEmail != null) {
                AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                Logger.debug("got primary email: " + primaryEmail);
                ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(primaryEmail.getEmail())).withMessage(new Message().withSubject(new Content().withData("JournWe Notification" + (notification.getSubject() != null && !"".equals(notification.getSubject()) ? ": " + notification.getSubject() : ""))).withBody(new Body().withText(new Content().withData(notification.getMessage())))).withSource("no-reply@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyUserViaEmailDigest(List<Notification> notifications, String digestType) {
        if(notifications.size() > 0)
            try {
                UserEmail primaryEmail = new UserEmailDAO().getPrimaryEmailOfUser(notifications.get(0).getUserId());
                if (primaryEmail != null) {
                    AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                            ConfigFactory.load().getString("aws.accessKey"),
                            ConfigFactory.load().getString("aws.secretKey")));
                    Logger.debug("got primary email: " + primaryEmail);
                    String message = "You have following Notifications: \n";
                    for(Notification notification : notifications)
                        message += "+ " + notification.getMessage() + "\n";

                    ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(primaryEmail.getEmail())).withMessage(new Message().withSubject(new Content().withData("JournWe Notification: " + digestType + " Digest")).withBody(new Body().withText(new Content().withData(message)))).withSource("no-reply@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
