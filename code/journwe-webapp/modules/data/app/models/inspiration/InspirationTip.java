package models.inspiration;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 23.10.13
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-inspiration-tip")
public class InspirationTip {

    private String inspirationId;

    private Long created;

    private String userId;

    private String tip;

    private boolean active;

    private String lang;

    @DynamoDBHashKey
    public String getInspirationId() {
        return inspirationId;
    }

    public void setInspirationId(String inspirationId) {
        this.inspirationId = inspirationId;
    }

    @DynamoDBRangeKey
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "lang-created-index")
    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @DynamoDBAttribute
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute
    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @DynamoDBIndexRangeKey(localSecondaryIndexName = "active-index", attributeName = "active")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "lang-created-index")
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}