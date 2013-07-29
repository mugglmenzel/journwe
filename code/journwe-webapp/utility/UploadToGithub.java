import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.util.HashMap;

public class UploadToGithub {

    public static void main(String[] args) {

        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJRJVMMAPP44IO3OA", "DlKBHrrhJb79Dwybx3vJEFAMXdV4aMoerHWb22o7");
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
                // login = username
                // password = yourpasswd
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
                System.out.println("Result: " + result);
            }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

}