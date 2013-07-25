package models.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import models.helpers.EnumMarshaller;

@DynamoDBTable(tableName = "journwe-adventure-authorization")
public class AdventureAuthorization {

    private String adventureId;

    private String userId;

    private EAuthorizationRole authorizationRole;

    @DynamoDBHashKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBRangeKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBMarshalling(marshallerClass = AuthorizationMarshaller.class)
    public EAuthorizationRole getAuthorizationRole() {
        return authorizationRole;
    }

    public void setAuthorizationRole(EAuthorizationRole authorizationRole) {
        this.authorizationRole = authorizationRole;
    }

    public static class AuthorizationMarshaller extends EnumMarshaller<EAuthorizationRole> {
    }
}
