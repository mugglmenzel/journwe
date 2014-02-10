package com.journwe;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.journwe.model.*;
import com.journwe.model.Message;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.01.14
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public class JournWeSMTPServer {

    private static BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJGCLA5GISOJ6UVZA", "UW3EB5LRrsTwkpTdRR/zU11v2xtMx919TLYIbsYM");
    private static DynamoDBMapper dynamo;
    private static AmazonS3Client s3;
    private static AmazonSimpleEmailServiceClient ses;
    private static String s3bucketName = "journwe-email-attachments";

    private static Logger logger = LoggerFactory.getLogger(JournWeSMTPServer.class);

    public static void main(String[] param) {
        System.out.println("Logger: " + logger + ", info: " + logger.isInfoEnabled() + ", error: " + logger.isErrorEnabled() + ", warn: " + logger.isWarnEnabled() + ", debug: " + logger.isDebugEnabled());

        logger.info("Server starting...");

        //DYNAMO
        final AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials);
        client.setEndpoint("dynamodb.us-east-1.amazonaws.com");
        dynamo = new DynamoDBMapper(client);

        //S3
        s3 = new AmazonS3Client(credentials);

        //SES
        ses = new AmazonSimpleEmailServiceClient(credentials);


        //SMTP SERVER
        SMTPServer server = new SMTPServer(new SimpleMessageListenerAdapter(new SimpleMessageListener() {
            @Override
            public boolean accept(String from, String recipient) {
                logger.info("Got from: " + from + ", to: " + recipient);
                return dynamo.load(Adventure.class, recipient.substring(0, recipient.indexOf("@"))) != null;
            }

            @Override
            public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {

                try {
                    MimeMessage mimemsg = new MimeMessage(Session.getDefaultInstance(System.getProperties()), data);

                    //Save Message
                    Message msg = new Message();
                    msg.setAdventureId(recipient.substring(0, recipient.indexOf("@")));
                    msg.setSender(from);
                    msg.setBody("");

                    msg.setSubject(mimemsg.getSubject());

                    if (mimemsg.getReceivedDate() != null)
                        msg.setTimestamp(new Long(mimemsg.getReceivedDate().getTime()).toString());
                    else msg.setTimestamp(new Long(new Date().getTime()).toString());

                    processPart(msg, mimemsg);

                    dynamo.save(msg);
                    logger.info("Saved Message " + msg.getMessageId());


                    //Forward message

                    Adventurer hashKey = new Adventurer();
                    hashKey.setAdventureId(msg.getAdventureId());
                    DynamoDBQueryExpression<Adventurer> queryAdvr = new DynamoDBQueryExpression<Adventurer>().withHashKeyValues(hashKey);

                    for (Adventurer advr : dynamo.query(Adventurer.class, queryAdvr)) {

                        UserEmail ue = new UserEmail();
                        ue.setUserId(advr.getUserId());
                        DynamoDBQueryExpression queryEmail = new DynamoDBQueryExpression().withHashKeyValues(ue);

                        Iterator<UserEmail> results = dynamo.query(UserEmail.class, queryEmail).iterator();
                        UserEmail result = null;
                        while (results.hasNext()) {
                            result = results.next();
                            if (result.isPrimary()) break;
                        }

                        if (result != null) {
                            mimemsg.setFrom(new InternetAddress(recipient));
                            mimemsg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(result.getEmail()));
                            mimemsg.setReplyTo(new Address[]{new InternetAddress(from), new InternetAddress(recipient)});
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            mimemsg.writeTo(outputStream);
                            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

                            ses.sendRawEmail(new SendRawEmailRequest().withRawMessage(rawMessage).withDestinations(result.getEmail()).withSource(msg.getAdventureId() + "@journwe.com"));
                        }
                    }

                    logger.info("Forwarded Message " + msg.getMessageId());

                    //ses.sendEmail(new SendEmailRequest().withDestination(dest).withMessage(new com.amazonaws.services.simpleemail.model.Message().withSubject(new Content().withData(msg.getSubject())).withBody(new Body().withHtml(new Content().withData(msg.getBody())).withText(new Content().withData(msg.getBody())))));

                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            /**
             * Process the data in the MultiPart.
             */
            private void processMultiPart(Message msg, Part part)
                    throws Exception {
                if (part.isMimeType("multipart/*")) {
                    Multipart multipart = (Multipart) part.getContent();
                    for (int i = 0; i < multipart.getCount(); i++)
                        processPart(msg, multipart.getBodyPart(i));
                }
            }

            /**
             * Process a Part.
             */
            private void processPart(Message msg, Part part)
                    throws Exception {
                if (part.isMimeType("multipart/*")) {
                    processMultiPart(msg, part);
                } else {
                    if (part.getFileName() != null) {
                        logger.info("File with fileName: " + part.getFileName());
                        saveAttachmentToS3(msg, part);

                        MessageAttachment attachment = new MessageAttachment();
                        attachment.setMessageId(msg.getMessageId());
                        attachment.setFileName(part.getFileName());
                        dynamo.save(attachment);

                    } else if (part.getContent() != null && part.getContent() instanceof String) {
                        logger.debug("Appending Text: " + ((String) part.getContent()).trim());
                        msg.setBody(msg.getBody() + (String) part.getContent());
                    }
                }
            }

            /**
             * Saves the content of the part to an S3 object.
             *
             * @throws MessagingException
             * @throws IOException
             */
            private void saveAttachmentToS3(Message msg, Part part)
                    throws MessagingException, IOException {

                logger.info("Save " + part.getFileName() + " in bucket " + s3bucketName);
                InputStream is = part.getInputStream();

                final String s3ObjectName = msg.getMessageId() + "/" + part.getFileName();
                logger.debug("S3 Object Name: " + s3ObjectName);

                ObjectMetadata metadata = new ObjectMetadata();
                Long contentLength = Long.valueOf(IOUtils.toByteArray(is).length);
                metadata.setContentLength(contentLength);
                metadata.setContentType(part.getContentType().contains(";") ? part.getContentType().substring(0, part.getContentType().indexOf(";")) : part.getContentType());
                is.reset();
                logger.debug("Set S3 File Meta-data to length: " + contentLength + ", and content-type: " + part.getContentType());

                TransferManager tx = new TransferManager(s3);
                Upload upload = tx.upload(s3bucketName, s3ObjectName, is, metadata);
                upload.addProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(ProgressEvent progressEvent) {
                        logger.debug("Uploaded " + progressEvent.getBytesTransferred() + " bytes of " + s3ObjectName);
                    }
                });
            }


        }));
        server.setEnableTLS(false);
        server.setPort(2525);
        server.start();
    }


}
