package models.dao.common;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.typesafe.config.ConfigFactory;

public class PersistenceHelper {

	private static AmazonDynamoDB db;

	public static DynamoDBMapper getManager() {
        db = getDynamoDBClient();
		return new DynamoDBMapper(db);
	}

    public static AmazonDynamoDB getDynamoDBClient() {
        if (db == null) {
            db = new AmazonDynamoDBClient(new BasicAWSCredentials(ConfigFactory
                    .load().getString("aws.accessKey"), ConfigFactory.load()
                    .getString("aws.secretKey")));
            db.setEndpoint(ConfigFactory.load().getString("aws.endpoint"));
        }
        return db;
    }

}
