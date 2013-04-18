package models;

import com.amazonaws.services.dynamodb.datamodeling.*;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 16.04.13
 * Time: 17:35
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-usersocial")
public class UserSocial {


    private String userId;

    private String provider;

    private String socialId;



    @DynamoDBHashKey
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @DynamoDBRangeKey
    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    @DynamoDBAttribute
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
