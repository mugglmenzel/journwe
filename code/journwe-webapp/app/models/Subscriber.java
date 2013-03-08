package models;

import play.data.validation.Constraints.Email;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="journwe-subscriber")
public class Subscriber {
	
	@Email
	private String email;

	/**
	 * @return the email
	 */
	@DynamoDBHashKey
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	
}
