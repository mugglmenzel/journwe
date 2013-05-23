package com.journwe.ae;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class AmazonSqsDispatcher {
	
	// journwe-appengine user in AWS IAM
	private static String accessKey = "AKIAJU6BC4L6L6XMUXEQ";
    private static String secretKey = "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";
    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/687997543308/journwe-mail-inbox";
    private static AmazonSQS sqs = null;
    
    static {
    	init();
    }
    
	public static void init() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey,
                secretKey);
        // get the SQS service
        sqs = new AmazonSQSClient(credentials);
	}
	
	protected static void sendMessage(String text) {
        SendMessageRequest req = new SendMessageRequest(QUEUE_URL,
                text);
        req.setMessageBody("Message body: "+text);
		sqs.sendMessage(req );
    }

}
