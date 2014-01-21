package com.journwe.utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import net.dharwin.common.tools.cli.api.CLIContext;
import net.dharwin.common.tools.cli.api.Command;
import net.dharwin.common.tools.cli.api.CommandResult;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.beust.jcommander.Parameter;

/**
 * A helper class for deleting content from DynamoDB. Be careful. By default,
 * the dev aws credentials are set.
 * 
 * @author markus
 * 
 */
@CLICommand(name = "delete-journwedb-records", description = "Delete table records from journwe db.")
public class CleanJournweDBCommand extends Command<JournweCLIContext> {

	@Parameter(names = { "-t", "--table" }, description = "Table name. Delete all table contents with: all", required = true)
	private String whichTables;

	@Override
	public CommandResult innerExecute(JournweCLIContext context) {
		
		System.out.println("Prepare to delete records ...");

		Properties prop = new Properties();
		String endpoint = "dynamodb.eu-west-1.amazonaws.com";
		String accesskey = "NOT_SET";
		String secret = "NOT_SET";
		try {
			// load a properties file from class path, inside static method
			prop.load(CleanJournweDBCommand.class.getClassLoader()
					.getResourceAsStream("aws.dev.properties"));

			accesskey = prop.getProperty("accesskey");
			secret = prop.getProperty("secret");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("Loaded AWS credentials of "+endpoint);
		
		BasicAWSCredentials credentials = new BasicAWSCredentials(accesskey,
				secret);
		AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient(credentials);
		dynamoDB.setEndpoint(endpoint);

		String[] tables = new String[] {};
		if (whichTables.equals("all")) {
			tables = new String[] { "journwe-adventure",
					"journwe-adventurer", "journwe-adventure-authorization",
					"journwe-adventure-reminder", "journwe-adventure-to-journwe-category",
					"journwe-adventure-to-journwe-user",
					"journwe-category-hierarchy", "journwe-category-hierarchy-count",
					"journwe-category-to-journwe-adventure",
					"journwe-category-to-journwe-inspiration",
					"journwe-inspiration-tip", "journwe-james-adventuremail",
					"journwe-notification",
					"journwe-user-to-journwe-adventure",
					"journwe-notification-digestqueue-item", 
					"journwe-category", "journwe-comment",
					"journwe-commentthread", "journwe-file",
					"journwe-inspiration", "journwe-placeoption",
					"journwe-placepreference", "journwe-subscriber",
					"journwe-timeoption", "journwe-timepreference",
					"journwe-todo", "journwe-user", "journwe-useremail",
					"journwe-james-forwardmailto", 
					"journwe-usersocial",
					"journwe-usertokenaction"};
		} else {
			tables = new String[] { whichTables };
		}
		
		System.out.println("Delete records in: ");
		for (String tableName : tables) {
			System.out.println("* "+tableName);
		}

		String keyName = "";
		String rangeKeyName = "";

		for (String tableName : tables) {
			if (tableName.equals("journwe-adventure")
					|| tableName.equals("journwe-adventurehash")
					|| tableName.equals("journwe-category")
					|| tableName.equals("journwe-inspiration")
					|| tableName.equals("journwe-todo")
					|| tableName.equals("journwe-user")) {
				keyName = "id";
			}
			if (tableName.equals("journwe-adventure-authorization")
					|| tableName.equals("journwe-adventurer")
					|| tableName.equals("journwe-commentthread")
					|| tableName.equals("journwe-file")
					|| tableName.equals("journwe-placeoption")
					|| tableName.equals("journwe-timeoption")) {
				keyName = "adventureId";
			}
			if (tableName.equals("journwe-adventureshortname")) {
				keyName = "shortname";
			}
			if (tableName.equals("journwe-adventure-reminder")) {
				keyName = "type";
			}
			if (tableName.equals("journwe-adventure-to-journwe-category")) {
				keyName = "journwe-adventure-id";
				rangeKeyName = "journwe-category-id";
			}
			if (tableName.equals("journwe-adventure-to-journwe-user")) {
				keyName = "journwe-adventure-id";
				rangeKeyName = "journwe-user-id";
			}
			if (tableName.equals("journwe-category-hierarchy")) {
				keyName = "superCategoryId";
				rangeKeyName = "subCategoryId";
			}
			if (tableName.equals("journwe-category-hierarchy-count")) {
				keyName = "categoryId";
			}
			if (tableName.equals("journwe-category-to-journwe-adventure")) {
				keyName = "journwe-category-id";
				rangeKeyName = "journwe-adventure-id";
			}
			if (tableName.equals("journwe-category-to-journwe-inspiration")) {
				keyName = "journwe-category-id";
				rangeKeyName = "journwe-inspiration-id";
			}
			if (tableName.equals("journwe-inspiration-tip")) {
				keyName = "inspirationId";
				rangeKeyName = "created";
			}
			if (tableName.equals("journwe-inspiration-to-journwe-category")) {
				keyName = "journwe-inspiration-id";
				rangeKeyName = "journwe-category-id";
			}
			if (tableName.equals("journwe-james-adventuremail")) {
				keyName = "adventureId";
				rangeKeyName = "timestamp";
			}
			if (tableName.equals("journwe-james-forwardmailto")) {
				keyName = "adventureId";
			}
			if (tableName.equals("journwe-notification")) {
				keyName = "userId";
				rangeKeyName = "created";
			}
			if (tableName.equals("journwe-notification-digestqueue-item")) {
				keyName = "digestQueueName";
				rangeKeyName = "userId";
			}
			if (tableName.equals("journwe-user-to-journwe-adventure")) {
				keyName = "journwe-user-id";
				rangeKeyName = "journwe-adventure-id";
			}
			if (tableName.equals("journwe-usertokenaction")) {
				keyName = "type";
				rangeKeyName = "token";
			}
			if (tableName.equals("journwe-comment")) {
				keyName = "threadId";
				rangeKeyName = "timestamp";
			}
			if (tableName.equals("journwe-commentthread")) {
				rangeKeyName = "topicType";
			}
			if (tableName.equals("journwe-adventure-reminder")) {
				rangeKeyName = "adventureId";
			}			
			if (tableName.equals("journwe-file")) {
				rangeKeyName = "fileName";
			}
			if (tableName.equals("journwe-adventurer")) {
				rangeKeyName = "userIdRangeKey";
			}
			if (tableName.equals("journwe-adventure-authorization")) {
				rangeKeyName = "userId";
			}
			if (tableName.equals("journwe-placeoption") ) {
				rangeKeyName = "placeId";
			}
			if (tableName.equals("journwe-placepreference")) {
				keyName = "placeoptionid";
				rangeKeyName = "adventurerid";
			}
			if (tableName.equals("journwe-subscriber")) {
				keyName = "email";
			}
			if (tableName.equals("journwe-timeoption")) {
				rangeKeyName = "timeId";
			}
			if (tableName.equals("journwe-timepreference")) {
				keyName = "timeoptionid";
				rangeKeyName = "adventurerid";
			}
			if (tableName.equals("journwe-todo")) {
				rangeKeyName = "adventureId";
			}
			if (tableName.equals("journwe-useremail")) {
				keyName = "userId";
				rangeKeyName = "email";
			}
			if (tableName.equals("journwe-usersocial")) {
				keyName = "provider";
				rangeKeyName = "socialId";
			}

			try {
				// Get items in table
				ScanRequest scanRequest = new ScanRequest(tableName);
				ScanResult scanResult = dynamoDB.scan(scanRequest);
				System.out.println("Preparing delete items in table "+tableName);
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
					DeleteItemRequest request = new DeleteItemRequest(
							tableName, key) {
					};
					DeleteItemResult result = dynamoDB.deleteItem(request);
					System.out.println("DELETED RECORD " + key);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return CommandResult.ERROR;
			}
		}
		return CommandResult.OK;

	}
}