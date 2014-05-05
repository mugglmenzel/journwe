package helpers.inviters;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.typesafe.config.ConfigFactory;
import models.adventure.Adventure;

/**
 * Created by mugglmenzel on 05/05/14.
 */
public abstract class AbstractInvitation {

    private static final AWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey"));


    public void send(String inviteeContact, String inviterName, Adventure adv, String shortURL) throws Exception {
        if (getInviteeEmail(inviteeContact) != null)
            new AmazonSimpleEmailServiceClient(credentials).sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(getInviteeEmail(inviteeContact))).withMessage(new Message().withSubject(new Content().withData("Invitation to the JournWe " + adv.getName())).withBody(new Body().withText(new Content().withData((getInviteeName(inviteeContact) != null && !"".equals(getInviteeName(inviteeContact)) ? "Hi " + getInviteeName(inviteeContact) : "Hey") + ",\nYour friend " + inviterName + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + shortURL + " to participate in that great adventure.\n\nJournWe.com")))).withSource(adv.getId() + "@journwe.com").withReplyToAddresses(adv.getId() + "@journwe.com"));
    }

    public abstract String getInviteeName(String inviteeContact) throws Exception;

    public abstract String getInviteeEmail(String inviteeContact) throws Exception;
}
