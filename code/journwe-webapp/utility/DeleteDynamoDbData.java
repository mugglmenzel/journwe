import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.HashMap;

public class DeleteDynamoDbData {

    public static void main(String[] args) {
        String tableName = args[1];
        String keyName = args[2];
        String rangeKeyName = "";

        if(args[0].equals("range")) {
            rangeKeyName = args[3];
        }

        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJRJVMMAPP44IO3OA", "DlKBHrrhJb79Dwybx3vJEFAMXdV4aMoerHWb22o7");
        AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient(credentials);
        // Get items in table
        ScanRequest scanRequest = new ScanRequest(tableName);
        ScanResult scanResult = dynamoDB.scan(scanRequest);

        for (int i = 0; i < scanResult.getItems().size(); i++) {
            HashMap<String, AttributeValue> item = (HashMap<String, AttributeValue>) scanResult
                    .getItems().get(i);

            HashMap key = new HashMap<String, AttributeValue>();
            AttributeValue keyValue;
            keyValue = item.get(keyName);
            key = new HashMap<String, AttributeValue>();
            key.put(keyName, keyValue);
            if(rangeKeyName!="") {
                keyValue = item.get(rangeKeyName);
                key.put(rangeKeyName, keyValue);
            }
            DeleteItemRequest request = new DeleteItemRequest(tableName, key) {
            };
            DeleteItemResult result = dynamoDB.deleteItem(request);
            System.out.println("Result: " + result);
        }
    }
}