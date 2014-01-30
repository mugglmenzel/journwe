package com.journwe;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.journwe.model.Adventure;
import com.journwe.model.Message;
import com.journwe.model.MessageAttachment;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
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

    private static BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAIZHEDHEBQSHDQGCA", "DOxVc+qo/K0pGUZP7uoksKPPuAZsLa2gH89CFgmO");
    private static DynamoDBMapper dynamo;
    private static AmazonS3Client s3;
    private static String s3bucketName = "journwe-email-attachments";

    private static Logger logger = LoggerFactory.getLogger(JournWeSMTPServer.class);

    public static void main(String[] param) {
        System.out.println("Logger: " + logger + ", info: " + logger.isInfoEnabled() + ", error: " + logger.isErrorEnabled() + ", warn: " + logger.isWarnEnabled() + ", debug: " + logger.isDebugEnabled());

        logger.info("Server starting...");

        //DYNAMO
        final AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials);
        client.setEndpoint("dynamodb.eu-west-1.amazonaws.com");
        dynamo = new DynamoDBMapper(client);

        //S3
        s3 = new AmazonS3Client(credentials, new ClientConfiguration().withProtocol(Protocol.HTTP));


        //SMTP SERVER
        SMTPServer server = new SMTPServer(new SimpleMessageListenerAdapter(new SimpleMessageListener() {
            @Override
            public boolean accept(String from, String recipient) {
                logger.info("Got from: " + from + ", to: " + recipient);
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

                    if (mimemsg.getReceivedDate() != null)
                        msg.setTimestamp(new Long(mimemsg.getReceivedDate().getTime()).toString());
                    else msg.setTimestamp(new Long(new Date().getTime()).toString());

                    processPart(msg, mimemsg);

                    dynamo.save(msg);
                    logger.info("Saved Message " + msg.getMessageId());
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
                logger.info("S3 Object Name: " + s3ObjectName);

                ObjectMetadata metadata = new ObjectMetadata();
                Long contentLength = Long.valueOf(IOUtils.toByteArray(is).length);
                metadata.setContentLength(contentLength);
                metadata.setContentType(part.getContentType().contains(";") ? part.getContentType().substring(0, part.getContentType().indexOf(";")) : part.getContentType());
                is.reset();
                logger.info("Set S3 File Meta-data to length: " + contentLength + ", and content-type: " + part.getContentType());

                TransferManager tx = new TransferManager(s3);
                Upload upload = tx.upload(s3bucketName, s3ObjectName, is, metadata);
                upload.addProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(ProgressEvent progressEvent) {
                        logger.info("Uploaded " + progressEvent.getBytesTransferred() + " bytes of " + s3ObjectName);
                    }
                });
            }


        }));
        server.setEnableTLS(false);
        server.setPort(2525);
        server.start();
    }


}