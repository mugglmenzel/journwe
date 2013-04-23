package models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import play.data.validation.Constraints.Email;

@DynamoDBTable(tableName = "journwe-subscriber")
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
