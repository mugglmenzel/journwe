package services;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.feth.play.module.mail.Mailer;
import com.typesafe.config.ConfigFactory;
import controller.JournweMailer;
import models.dao.NotificationDAO;
import models.dao.NotificationDigestQueueDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserEmailDAO;
import models.notifications.ENotificationFrequency;
import models.notifications.ENotificationTopics;
import models.notifications.Notification;
import models.user.User;
import models.user.UserEmail;
import play.Logger;
import play.i18n.Lang;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 30.07.13
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */
public class UserNotifier {


    public void notifyUser(String userId, String message) {
        this.notifyUser(userId, ENotificationTopics.GENERAL, userId, message);
    }


    public void notifyUser(String userId, ENotificationTopics topic, String topicRef, String message) {
        this.notifyUser(userId, topic, topicRef, message, null);
    }


    public void notifyUser(String userId, ENotificationTopics topic, String topicRef, String message, String subject) {
        this.notifyUser(new UserDAO().get(userId), topic, topicRef, message, subject);
    }

    public void notifyUser(User user, String message) {
        this.notifyUser(user, ENotificationTopics.GENERAL, user.getId(), message);
    }

    public void notifyUser(User user, ENotificationTopics topic, String topicRef, String message) {
        this.notifyUser(user, topic, topicRef, message, null);
    }

    public void notifyUser(User user, ENotificationTopics topic, String topicRef, String message, String subject) {

        Notification noti = new Notification();
        noti.setUserId(user.getId());
        noti.setTopic(topic);
        noti.setTopicRef(topicRef);
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
                notifyUserViaEmailDigest(user.getId(), new NotificationDAO().unsent(user.getId()), frequency);
                user.setLastDigest(new Date());
                new UserDAO().save(user);

                new NotificationDigestQueueDAO().delete(frequency.getDigestName(), user.getId());
            }
        }
    }



    /* Email Stuff */

    public void notifyUserViaEmail(Notification notification) {
        //sendNotificationEmail(notification.getUserId(), "JournWe Notification" + (notification.getSubject() != null && !"".equals(notification.getSubject()) ? ": " + notification.getSubject() : ""), notification.getMessage());
        markNotificationSent(notification);
    }

    public void notifyUserViaEmailDigest(String userId, List<Notification> notifications, ENotificationFrequency frequency) {
        if (notifications.size() > 0) {
            Map<ENotificationTopics, Map<Object, List<Notification>>> notis = new HashMap<ENotificationTopics, Map<Object, List<Notification>>>();


            Map<Object, List<Notification>> objNotis;
            for (ENotificationTopics topic : ENotificationTopics.values()) {
                objNotis = new HashMap<Object, List<Notification>>();

                for (Notification notification : notifications) {
                    if (topic.equals(notification.getTopic())) {
                        Object obj = NotificationObjectResolver.get(topic, notification.getTopicRef());
                        List<Notification> nots = objNotis.get(obj) != null ? objNotis.get(obj) : new ArrayList<Notification>();
                        nots.add(notification);
                        objNotis.put(obj, nots);

                        markNotificationSent(notification);
                    }
                }

                notis.put(topic, objNotis);
            }
            Logger.debug("digest emailer is fed with " + notis);

            User usr = new UserDAO().get(userId);
            if (usr != null) {
                //TODO: user actual user LANG setting
                Mailer.Mail.Body emailBody = JournweMailer.getMailBody("email.notificationDigest", usr.getLanguage() != null ? usr.getLanguage() : new Lang(Lang.defaultLang()), new Object[]{notis, frequency});
                sendNotificationEmail(userId, "JournWe Notification: " + frequency.getDigestName() + " Digest", emailBody);
            }
        }

    }

    private void markNotificationSent(Notification notification) {
        notification.setSent(true);
        new NotificationDAO().save(notification);
    }


    private void sendNotificationEmail(String userId, String subject, Mailer.Mail.Body message) {
        try {
            UserEmail primaryEmail = new UserEmailDAO().getPrimaryEmailOfUser(userId);
            if (primaryEmail != null) {
                AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                Logger.debug("got primary email: " + primaryEmail);
                ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(primaryEmail.getEmail())).withMessage(new Message().withSubject(new Content().withData(subject)).withBody(new Body().withText(new Content().withData(message.getText())).withHtml(new Content().withData(message.getHtml())))).withSource("no-reply@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
