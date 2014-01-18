package models.user;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 16.04.13
 * Time: 17:35
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-usersocial")
public class UserSocial {

    private String socialId;

    private String userId;

    private String provider;
    
    private String accessToken;

    @DynamoDBHashKey
    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    @DynamoDBRangeKey
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "userId-index")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}
