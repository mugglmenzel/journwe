package models.inspiration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created by mugglmenzel on 16.02.14.
 */
@DynamoDBTable(tableName = "journwe-inspiration-url")
public class InspirationURL {

    private String inspirationId;

    private String url;

    private String description;

    @DynamoDBHashKey
    public String getInspirationId() {
        return inspirationId;
    }

    public void setInspirationId(String inspirationId) {
        this.inspirationId = inspirationId;
    }

    @DynamoDBRangeKey
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
