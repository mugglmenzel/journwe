package models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import play.data.validation.Constraints;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 16.04.13
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-useremail")
public class UserEmail {

    private String userId;

    @Constraints.Required
    private String email;

    private boolean primary;

    private boolean validated;


    @DynamoDBHashKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute
    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @DynamoDBAttribute
    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
