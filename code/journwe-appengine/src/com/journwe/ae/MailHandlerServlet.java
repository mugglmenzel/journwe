package com.journwe.ae;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * See https://developers.google.com/appengine/docs/java/mail/receiving
 * @author markus
 *
 */
public class MailHandlerServlet extends HttpServlet {

	private static final Logger logger = Logger
			.getLogger(MailHandlerServlet.class.getName());

	private static final long serialVersionUID = -1487816196632675955L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());
			Address[] addresses = message.getFrom();
			String sender = "";
			if (addresses != null) {
				for(Address address : addresses)
					sender += address.toString();
			}
			String contentType = message.getContentType();
			Object o = message.getContent();
			String content = "";

			if (o instanceof String) {
			content = ((String)o);
			}
			else if (o instanceof Multipart) {
			Multipart mp = (Multipart)o;

			int count = mp.getCount();
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < count; i++) {
				sb.append(mp.getBodyPart(i).getContent().toString());
			}
			content = sb.toString();
			}
			else if (o instanceof InputStream) {
			InputStream is = (InputStream)o;
			int c;
			StringBuffer sb2 = new StringBuffer("");
			while ((c = is.read()) != -1)
				sb2.append(c);
			content = sb2.toString();
			}

			AmazonSqsDispatcher.sendMessage("Sender: "+sender+ " | ContentType: "+contentType+ " | Content: "+content);

		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
}
