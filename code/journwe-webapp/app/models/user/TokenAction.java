package models.user;

import java.io.Serializable;
import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.dao.helpers.EnumMarshaller;
import models.user.ETokenType;
import models.user.EUserRole;
import models.user.User;
import play.data.format.Formats;


@DynamoDBTable(tableName = "journwe-usertokenaction")
public class TokenAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    //private String tokenId;

    // can only be range key because we must be able to query either by
    // token or by targetuserid
    private String token;

    // TODO
    // Many-to-One?? Why?
    private String targetUserId;

    // hash key
    private ETokenType type;

    private Date created = new Date();

    private Date expires;

	public boolean isValid() {
		return this.expires.after(new Date());
	}

    @DynamoDBRangeKey
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @DynamoDBAttribute
    @DynamoDBIndexRangeKey(attributeName="targetUserId",
            localSecondaryIndexName="targetUserId-index")
    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

//    @DynamoDBIndexRangeKey(attributeName="tokenType",
//            localSecondaryIndexName="tokenTypeIndex")

    @DynamoDBHashKey
    @DynamoDBMarshalling(marshallerClass = TokeTypeMarshaller.class)
    public ETokenType getType() {
        return type;
    }

    public void setType(ETokenType type) {
        this.type = type;
    }

    @DynamoDBAttribute
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @DynamoDBAttribute
    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public static class TokeTypeMarshaller extends EnumMarshaller<ETokenType> {
    }
}
