package com.journwe.james;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMailet;
import org.apache.mailet.base.RFC2822Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

public class AdventureMailet extends GenericMailet {
	private static final Logger logger = LoggerFactory
			.getLogger(AdventureMailet.class);
	private AmazonDynamoDBClient dynamoDB;
	private AmazonS3 s3;
	private MyAmazonSES myAmazonSES;
	
	String tableName = "journwe-james-adventuremail";
	String s3bucketName = "journwe-email-attachments";
	public static String _environment = "dev";

	public static void main(String[] args) {
		System.out
				.println("I am the AdventureMailet. Put me in $JAMES_HOME/conf/lib plz!");
	}

	public static String getEnvironment() {
		return _environment;
	}

	public static void setEnvironment(String environment) {
		AdventureMailet._environment = environment;
	}

	@Override
	public void init() throws MessagingException {
		super.init();
		// Create DynamoDB client
		String propertiesFileName = "aws.dev.properties";
		if (_environment.equalsIgnoreCase("prod"))
			propertiesFileName = "aws.prod.properties";
		Properties prop = new Properties();
		String accesskey = "NOT_SET";
		String secret = "NOT_SET";
		try {
			// Init DynamoDB Client
			prop.load(AdventureMailet.class.getClassLoader()
					.getResourceAsStream(propertiesFileName));
			accesskey = prop.getProperty("accesskey");
			secret = prop.getProperty("secret");
			BasicAWSCredentials credentials = new BasicAWSCredentials(
					accesskey, secret);
			dynamoDB = new AmazonDynamoDBClient(credentials);
			String dynamodbEndpoint = prop.getProperty("dynamoendpoint");
			dynamoDB.setEndpoint(dynamodbEndpoint);
			// Init S3 Client
			ClientConfiguration clientConfig = new ClientConfiguration();
			clientConfig.setProtocol(Protocol.HTTP);
			s3 = new AmazonS3Client(credentials, clientConfig);
			// S3 is global, endpoint shouldnt matter
			//String s3endpoint = prop.getProperty("s3endpoint");
			//s3.setEndpoint(s3endpoint);
			
			// Init SES SMPTP client
			myAmazonSES = new MyAmazonSES();
			myAmazonSES.init();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("AdventureMailet failed to init() correctly!");
			logger.error(e.getMessage());
		}
	}

	@Override
	public void service(Mail mail) throws MessagingException {
		System.out.println("Received Mail!");
		Collection<MailAddress> recipientsMailAddresses = mail.getRecipients();
		String adventureId = "";
		// MIME Message
		MimeMessage message = mail.getMessage();
		for (MailAddress recipientMailAddress : recipientsMailAddresses)
			adventureId = recipientMailAddress.getUser();
		System.out.println("Receipient == adventureId: " + adventureId);
		String sender = mail.getSender().toString();
		System.out.println("Sender: " + sender);
		String timestamp = ""+new Date().getTime();
		System.out.println("at time: " + timestamp);		
		String[] rcpts = message.getHeader(RFC2822Headers.TO);
		String[] ccRcpts = message.getHeader(RFC2822Headers.CC);
		String subject = message.getSubject();
		StringBuffer body = new StringBuffer("");
		List<String> bodyParts = new ArrayList<String>();
		List<String> attachmentS3ObjectNames = new ArrayList<String>();
		try {
			// Process message body and attachments.
			processPart(adventureId, message, sender, timestamp, bodyParts, attachmentS3ObjectNames);
			if(bodyParts.size()>0) {
				for(String part : bodyParts)
					body.append(part);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// String body = getStringFromMimeMessage(mimeMessage);

		// Save in DynamoDB
		if (subject == null || subject.isEmpty())
			subject = "";
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("adventureId", new AttributeValue().withS(adventureId));
		item.put("timestamp", new AttributeValue().withS(timestamp));
		item.put("sender", new AttributeValue().withS(sender));
		if(!subject.isEmpty())
			item.put("subject", new AttributeValue().withS(subject));
		if(!body.toString().isEmpty())
			item.put("body", new AttributeValue().withS(body.toString()));
		if(attachmentS3ObjectNames.size()>0)
			item.put("s3attachments", new AttributeValue().withSS(attachmentS3ObjectNames));

		PutItemRequest putItemRequest = new PutItemRequest().withTableName(
				tableName).withItem(item);
		PutItemResult result = dynamoDB.putItem(putItemRequest);
		System.out.println("Saved email in DynamoDB. Result = " + result.toString());
		
		// Forward E-Mail to all subscribed Adventurers
		List<String> newRecipients = new ArrayList<String>();
		Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		key.put("adventureId", new AttributeValue().withS(adventureId));
		GetItemRequest getItemRequest = new GetItemRequest()
				.withTableName("journwe-james-forwardmailto").withKey(key)
				.withAttributesToGet("forwardto");
		GetItemResult getItemResult = dynamoDB.getItem(getItemRequest);
		Map<String, AttributeValue> myitem = getItemResult.getItem();
		if (myitem != null && !myitem.isEmpty()) {
			for (Entry<String, AttributeValue> forwardtoitem : myitem.entrySet()) {
				if (forwardtoitem.getKey().equalsIgnoreCase("forwardto")) {
					AttributeValue forwardtoval = forwardtoitem.getValue();
					newRecipients = forwardtoval.getSS();
				}

			}
		}
		if(!newRecipients.isEmpty()) {
			message.setFrom(new InternetAddress(adventureId+"@journwe.com"));
			Address[] replyToAddresses = new Address[1];
			replyToAddresses[0] = new InternetAddress(sender);
			message.setReplyTo(replyToAddresses);
			Address[] addresses = new Address[newRecipients.size()];
			for(int i=0; i<newRecipients.size();i++) {
				String rec = newRecipients.get(i);
				if(!rec.endsWith("@journwe.com")) {
					addresses[i]=(new InternetAddress(rec));
					System.out.println("Send to "+rec);
				}
			}
			if(addresses.length>0) {
				message.setRecipients(Message.RecipientType.TO, addresses);
				myAmazonSES.send(message);
			}
		}
	}

	/**
	 * Process the data in the MultiPart.
	 */
	private void processMultiPart(String advId, Part part, String sender, String timestamp, List<String> bodyParts, List<String> s3attachments)
			throws Exception {
		if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int numParts = multipart.getCount();
			// Iterate over Parts
			for (int i = 0; i < numParts; i++) {
				Part p = multipart.getBodyPart(i);
			    processPart(advId, p, sender, timestamp, bodyParts, s3attachments);
			}
		}
	}

	/**
	 * Process a Part.
	 */
	private void processPart(String advId, Part part, String sender, String timestamp, List<String> bodyParts, List<String> s3attachments)
			throws Exception {
		if (part.isMimeType("multipart/*")) {
			processMultiPart(advId, part, sender, timestamp, bodyParts, s3attachments);
		} else {
			System.out.println("processPart");
			// if Part is a file
			String fileName = part.getFileName();
			if (fileName != null) {
				System.out.println("File with fileName: "+fileName);
				fileName = MimeUtility.decodeText(fileName);
				String s3ObjectName = saveAttachmentToS3(part, advId,sender, timestamp);
				s3attachments.add(s3ObjectName);
			} else {
				// if Part is string / text
				Object bodyObj = part.getContent();
				if (bodyObj != null && bodyObj instanceof String) {
					String bodyPart = (String) bodyObj;
					System.out.println("Text: "+bodyPart);
					bodyParts.add(bodyPart);
				}
			}
		}
	}

	/**
	 * Saves the content of the part to an S3 object.
	 * 
	 * @throws MessagingException
	 * @throws IOException
	 */
	private String saveAttachmentToS3(Part part, String advId, String sender, String timestamp)
			throws MessagingException, IOException {
		System.out.println("Save in bucket "+s3bucketName);
		String fileName = part.getFileName();
		InputStream is = part.getInputStream();
		// Generate an object name
		//DigestUtils.md5((sender+timestamp).getBytes());
		String s3ObjectName = advId + "/" +sender+"/"+timestamp+"/"+fileName;
		System.out.println("S3 Object Name: "+s3ObjectName);
		// and put object in bucket
		ObjectMetadata metadata = new ObjectMetadata();
		Long contentLength = Long.valueOf(IOUtils.toByteArray(is).length);
		metadata.setContentLength(contentLength);
		metadata.setContentType(part.getContentType());
		is.reset();
		PutObjectResult res = s3.putObject(s3bucketName, s3ObjectName, is,
				metadata);
		return s3ObjectName;
	}

}
