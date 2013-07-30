package com.journwe.utility;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import net.dharwin.common.tools.cli.api.CLIContext;
import net.dharwin.common.tools.cli.api.Command;
import net.dharwin.common.tools.cli.api.CommandResult;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

@CLICommand(name = "upload-github-issues", description = "Upload user issues, feature requests, and other feedback to our github repo.")
public class UploadToGithub extends Command<JournweCLIContext> {

    @Override
    public CommandResult innerExecute(JournweCLIContext context) {

    	Properties prop = new Properties(); 
    	String accesskey = "NOT_SET";
    	String secret = "NOT_SET";
    	try {
            //load a properties file from class path, inside static method
            prop.load(CleanJournweDBCommand.class.getClassLoader().getResourceAsStream("aws.properties"));

            accesskey = prop.getProperty("accesskey");
            secret = prop.getProperty("secret");
            } 
       catch (IOException ex) {
            ex.printStackTrace();
        }
    	BasicAWSCredentials credentials = new BasicAWSCredentials(accesskey, secret);
        AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient(credentials);

        String tableName = "journwe-admin-feedback";
        String keyName = "id";

            try {
            // Get items in table
            ScanRequest scanRequest = new ScanRequest(tableName);
            ScanResult scanResult = dynamoDB.scan(scanRequest);

            for (int i = 0; i < scanResult.getItems().size(); i++) {
                HashMap<String, AttributeValue> item = (HashMap<String, AttributeValue>) scanResult
                        .getItems().get(i);
                String feedbackText = item.get("text").getS();
                String feedbackType = item.get("feedbackType").getS();
                System.out.println("New Feedback: "+feedbackText+" ("+feedbackType+")");

                // Send item to github
                // store your credentials like this in /.github
                // oauth = ...
                GitHub github = GitHub.connect();
                System.out.println("You are connected with github as user " + github.getMyself());
                GHRepository repo = github.getRepository("mugglmenzel/journwe");
                System.out.println("Repo :"+repo);
                GHIssueBuilder issuebuilder = repo.createIssue("JournWe Website Feedback");
                issuebuilder.body(feedbackText);
                issuebuilder.label(feedbackType);
                issuebuilder.create();
                System.out.println("Sent feedback as an issue to Github.");

                HashMap key = new HashMap<String, AttributeValue>();
                AttributeValue keyValue;
                keyValue = item.get(keyName);
                key = new HashMap<String, AttributeValue>();
                key.put(keyName, keyValue);


                // then delete item from our table
                DeleteItemRequest request = new DeleteItemRequest(tableName, key) {
                };
                DeleteItemResult result = dynamoDB.deleteItem(request);
                System.out.println("DELETED FEEDBACK RECORD FROM DB.");
            }
            } catch(Exception e) {
                e.printStackTrace();
                return CommandResult.ERROR;
            }
            return CommandResult.OK;
        }

}