package com.journwe.bond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class BondHttpServer {

	private static final Logger logger = LoggerFactory
			.getLogger(BondHttpServer.class);

	private static HttpServer server;
	private static final int PORT = 17766;
	private static final String CREATE_EMAIL_USER_SERVICE = "/emailuser";
	private static AmazonSQSClient sqs;
	private static final String QUEUE_NAME = "journwe-email-bond";
	private static String _environment = "prod";

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out
					.println("Nothing happened. Must call service with at least one parameter of {start}.");
			System.exit(0);
		}
		// Start the service
		if (args[0].equals("start")) {
			logger.info("Receioved command START.");
			startService();
		} else {
			System.out.println("Sorry, you can only use the command(s): {start}.");
			System.exit(0);
		}
	}

//	private static void stopService() {
//		if (server != null) {
//			server.stop(PORT);
//			logger.info("Stopping server now.");
//			System.out.println("Stopping server now.");
//		} else {
//			System.out.println("Server was not started (was null).");
//		}
//	}
	


	private static void startService() throws IOException {
		logger.info("Starting HTTP server...");

		// Create DynamoDB client
		String propertiesFileName = "aws.dev.properties";
		if (_environment.equalsIgnoreCase("prod"))
			propertiesFileName = "aws.prod.properties";
		Properties prop = new Properties();
		String accesskey = "NOT_SET";
		String secret = "NOT_SET";
		try {
			// load a properties file from class path, inside static method
			prop.load(BondHttpServer.class.getClassLoader()
					.getResourceAsStream(propertiesFileName));
			accesskey = prop.getProperty("accesskey");
			secret = prop.getProperty("secret");
			BasicAWSCredentials credentials = new BasicAWSCredentials(
					accesskey, secret);
			sqs = new AmazonSQSClient(credentials, new ClientConfiguration()
					.withMaxErrorRetry(5).withMaxConnections(150));
			String endpoint = prop.getProperty("sqsendpoint");
			sqs.setEndpoint(endpoint);
			logger.info("These queues are in SQS in " + endpoint);
			logger.info(sqs.listQueues().toString());
			// System.out.println("These messages are in "+QUEUE_NAME+":");
			// System.out.println(sqs.receiveMessage(receiveMessageRequest));
		} catch (Exception e) {
			logger.error("BondHttpServer failed to start correctly!");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		// Start HTTP Server
		server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext(CREATE_EMAIL_USER_SERVICE,
				new CreateEmailUserHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		logger.info("Started HTTP server on port " + PORT);
	}

	static class CreateEmailUserHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			boolean ok = false;
			// Map<String, Object> parameters =
			// HttpRequestParser.parsePostParameters(t);
			// final String adventureid =
			// parameters.get("adventureid").toString();

			// Check SQS for new adventureids

			String adventureid = "";
			String receiptHandle = "";

			Integer qlength = new Integer(sqs
					.getQueueAttributes(
							new GetQueueAttributesRequest().withQueueUrl(
									QUEUE_NAME).withAttributeNames(
									"ApproximateNumberOfMessages"))
					.getAttributes().get("ApproximateNumberOfMessages"));
			logger.debug("Queue length: " + qlength);
			while (qlength > 0) {
				if (qlength > 10)
					qlength = 10;
				List<Message> sqsresult = sqs.receiveMessage(
						new ReceiveMessageRequest().withQueueUrl(QUEUE_NAME)
								.withMaxNumberOfMessages(qlength))
						.getMessages();
				for (Message m : sqsresult) {
					logger.debug("Message body: " + m.getBody());
					adventureid = m.getBody();
					receiptHandle = m.getReceiptHandle();

					final String addusercmd = "sudo /home/ubuntu/james/bin/james-cli.sh adduser "
							+ adventureid
							+ "@journwe.com grumpycat -h localhost -p 9999";
					Process proc = Runtime.getRuntime().exec(addusercmd);
					BufferedReader read = new BufferedReader(
							new InputStreamReader(proc.getInputStream()));
					String cmdresponse = read.readLine();
					logger.debug(cmdresponse);
					String okPrefix = "adduser command executed sucessfully";
					if (cmdresponse.startsWith(okPrefix)) {
						ok = true;
						logger.debug("Creating email user " + adventureid
								+ " was a success.");
					} else {
						logger.error("Creating email user " + adventureid
								+ " has failed.");
					}
					DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest()
							.withQueueUrl(QUEUE_NAME).withReceiptHandle(
									receiptHandle);
					sqs.deleteMessage(deleteMessageRequest);
					// System.out.println("Deleted message from SQS.");

					// Check the queue length
					qlength = new Integer(
							sqs.getQueueAttributes(
									new GetQueueAttributesRequest()
											.withQueueUrl(QUEUE_NAME)
											.withAttributeNames(
													"ApproximateNumberOfMessages"))
									.getAttributes()
									.get("ApproximateNumberOfMessages"));
					// If you receive 0 messages in an SQS request, you should
					// also stop:
					if (sqsresult.size() < 1)
						qlength = 0;

					logger.debug("Queue length: " + qlength);
					// Throttle throughput to prevent Denial of Service attacks.
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			// Return 200 OK
			t.sendResponseHeaders(200, "OK".length());
			OutputStream os = t.getResponseBody();
			os.write("OK".getBytes());
			os.close();

		}
	}

}
