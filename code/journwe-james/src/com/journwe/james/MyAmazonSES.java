package com.journwe.james;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAmazonSES {

	private static final Logger logger = LoggerFactory
			.getLogger(MyAmazonSES.class);

	// Supply your SMTP credentials below. Note that your SMTP credentials are
	// different from your AWS credentials.
	private static String SMTP_USERNAME = "YOUR_SMTP_USERNAME"; // Replace with
																// your SMTP
																// username.
	private static String SMTP_PASSWORD = "YOUR_SMTP_PASSWORD"; // Replace with
																// your SMTP
																// password.

	// Amazon SES SMTP host name.
	static final String HOST = "email-smtp.us-east-1.amazonaws.com";

	// Port we will connect to on the Amazon SES SMTP endpoint. We are choosing
	// port 25 because we will use
	// STARTTLS to encrypt the connection.
	static final int PORT = 25;

	public void init() {
		// Create SES client
		String propertiesFileName = "aws.dev.properties";
		if (AdventureMailet._environment.equalsIgnoreCase("prod"))
			propertiesFileName = "aws.prod.properties";
		Properties prop = new Properties();
		try {
			// Set smtp credentials
			prop.load(AdventureMailet.class.getClassLoader()
					.getResourceAsStream(propertiesFileName));
			SMTP_USERNAME = prop.getProperty("sessmtpuser");
			SMTP_PASSWORD = prop.getProperty("sessmtppasswd");

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("AdventureMailet failed to init() correctly!");
			logger.error(e.getMessage());
		}
	}

	public void send(final MimeMessage msg) {
		// Create a Properties object to contain connection configuration
		// information.
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", PORT);

		// Set properties indicating that we want to use STARTTLS to encrypt the
		// connection.
		// The SMTP session will begin on an unencrypted connection, and then
		// the client
		// will issue a STARTTLS command to upgrade to an encrypted connection.
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		// Create a Session object to represent a mail session with the
		// specified properties.
		Session session = Session.getDefaultInstance(props);

		Transport transport = null;
		// Send the message.
		try {

			// Create a transport.
			transport = session.getTransport();

			System.out
					.println("Attempting to send an email through the Amazon SES SMTP interface...");

			// Connect to Amazon SES using the SMTP username and password you
			// specified above.
			transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

			// Send the email.
			transport.sendMessage(msg, msg.getAllRecipients());
			System.out.println("Email sent!");
		} catch (Exception ex) {
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + ex.getMessage());
		} finally {
			// Close and terminate the connection.
			if(transport!=null)
				try {
					transport.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
		}
	}

}
