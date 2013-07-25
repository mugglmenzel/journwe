import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.HashMap;

public class DeleteDynamoDbData {

    public static void main(String[] args) {
        //String tableName = args[1];
//        String keyName = args[2];
//        String rangeKeyName = "";
//
//        if(args[0].equals("range")) {
//            rangeKeyName = args[3];
//        }

        String keyName = "";
        String rangeKeyName = "";

        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJRJVMMAPP44IO3OA", "DlKBHrrhJb79Dwybx3vJEFAMXdV4aMoerHWb22o7");
        AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient(credentials);

        for (String tableName : new String[]{"journwe-adventure", "journwe-adventurehash", "journwe-adventurepart",
                "journwe-adventurer", "journwe-adventureshortname", "journwe-category", "journwe-comment",
                "journwe-commentthread", "journwe-file", "journwe-inspiration", "journwe-placeoption",
                "journwe-placepreference", "journwe-subscriber", "journwe-timeoption", "journwe-timepreference",
                "journwe-todo", "journwe-user", "journwe-useremail", "journwe-usersocial"}) {
            if (tableName.equals("journwe-adventure") || tableName.equals("journwe-adventurehash") ||
                    tableName.equals("journwe-category") || tableName.equals("journwe-inspiration") ||
                    tableName.equals("journwe-todo") || tableName.equals("journwe-user")) {
                keyName = "id";
            }
            if (tableName.equals("journwe-adventurepart") || tableName.equals("journwe-adventurer") ||
                    tableName.equals("journwe-commentthread") || tableName.equals("journwe-file") ||
                    tableName.equals("journwe-placeoption") || tableName.equals("journwe-timeoption")) {
                keyName = "adventureId";
            }
            if(tableName.equals("journwe-adventureshortname")) {
                keyName = "shortname";
            }
            if(tableName.equals("journwe-comment")) {
                keyName = "threadId";
                rangeKeyName = "timestamp";
            }
            if(tableName.equals("journwe-commentthread")) {
                rangeKeyName = "topicType";
            }
            if(tableName.equals("journwe-file")) {
                rangeKeyName = "fileName";
            }
            if(tableName.equals("journwe-adventurer")) {
                rangeKeyName = "userId";
            }
            if(tableName.equals("journwe-placeoption")) {
                rangeKeyName = "placeId";
            }
            if(tableName.equals("journwe-placepreference")) {
                keyName = "placeoptionid";
                rangeKeyName = "adventurerid";
            }
            if(tableName.equals("journwe-subscriber")) {
                keyName = "email";
            }
            if(tableName.equals("journwe-timeoption")) {
                rangeKeyName = "timeId";
            }
            if(tableName.equals("journwe-timepreference")) {
                keyName = "timeoptionid";
                rangeKeyName = "adventurerid";
            }
            if(tableName.equals("journwe-todo")) {
                rangeKeyName = "adventureId";
            }
            if(tableName.equals("journwe-useremail")) {
                keyName = "userId";
                rangeKeyName = "email";
            }
            if(tableName.equals("journwe-usersocial")) {
                keyName = "provider";
                rangeKeyName = "socialId";
            }

            try {
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
                if (rangeKeyName != "") {
                    keyValue = item.get(rangeKeyName);
                    key.put(rangeKeyName, keyValue);
                }
                DeleteItemRequest request = new DeleteItemRequest(tableName, key) {
                };
                DeleteItemResult result = dynamoDB.deleteItem(request);
                System.out.println("Result: " + result);
            }
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Table: "+ tableName);

            }
        }
    }
}