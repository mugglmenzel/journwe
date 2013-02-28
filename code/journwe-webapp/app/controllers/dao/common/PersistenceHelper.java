package controllers.dao.common;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.typesafe.config.ConfigFactory;

public class PersistenceHelper {

	private static AmazonDynamoDB db;

	public static DynamoDBMapper getManager() {

		if (db == null)
			db = new AmazonDynamoDBClient(new BasicAWSCredentials(ConfigFactory
					.load().getString("aws.accessKey"), ConfigFactory.load()
					.getString("aws.secretKey")));
		return new DynamoDBMapper(db);

	}

}
