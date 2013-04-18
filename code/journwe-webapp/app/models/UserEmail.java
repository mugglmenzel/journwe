package models;

import com.amazonaws.services.dynamodb.datamodeling.*;
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
    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
