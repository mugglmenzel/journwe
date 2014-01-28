package com.journwe;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.journwe.model.Adventure;
import com.journwe.model.Message;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.01.14
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public class JournWeSMTPServer {

    public static void main(String[] param) {
        LoggerFactory.getLogger(JournWeSMTPServer.class).debug("Server starting...");

        //DYNAMO
        final AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAJGCLA5GISOJ6UVZA", "UW3EB5LRrsTwkpTdRR/zU11v2xtMx919TLYIbsYM"));
        client.setEndpoint("dynamodb.eu-west-1.amazonaws.com");
        final DynamoDBMapper dynamo = new DynamoDBMapper(client);


        //SMTP SERVER
        SMTPServer server = new SMTPServer(new SimpleMessageListenerAdapter(new SimpleMessageListener() {
            @Override
            public boolean accept(String from, String recipient) {
                System.out.println("Got from: " + from + ", to: " + recipient);
                return dynamo.load(Adventure.class, recipient.substring(0, recipient.indexOf("@"))) != null;
            }

            @Override
            public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {
                Message msg = new Message();
                msg.setAdventureId(recipient.substring(0, recipient.indexOf("@")));
                msg.setSender(from);
                msg.setBody("");

                try {
                    MimeMessage mimemsg = new MimeMessage(Session.getDefaultInstance(System.getProperties()), data);
                    msg.setSubject(mimemsg.getSubject());
                    if (mimemsg.getContent() instanceof String) msg.setBody((String) mimemsg.getContent());
                    else {
                        Multipart multipart = (Multipart) mimemsg.getContent();
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart part = multipart.getBodyPart(0);
                            if (part.getContent() instanceof String)
                                msg.setBody(msg.getBody() + (String) part.getContent());
                        }
                    }
                    if(mimemsg.getReceivedDate() != null) msg.setTimestamp(new Long(mimemsg.getReceivedDate().getTime()).toString());
                    else msg.setTimestamp(new Long(new Date().getTime()).toString());

                    dynamo.save(msg);
                    System.out.println("Saved Message " + msg);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        }));
        server.setEnableTLS(false);
        server.setPort(25);
        server.start();
    }

}
