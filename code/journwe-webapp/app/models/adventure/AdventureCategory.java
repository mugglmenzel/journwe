package models.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 10.09.13
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-adventure-category")
public class AdventureCategory {

    private String categoryId;

    private String adventureId;

    @DynamoDBHashKey
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @DynamoDBRangeKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }
}
