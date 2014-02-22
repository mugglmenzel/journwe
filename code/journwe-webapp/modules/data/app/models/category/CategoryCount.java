package models.category;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 19.08.13
 * Time: 01:52
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-category-hierarchy-count")
public class CategoryCount {

    private String categoryId;

    private Integer count;

    @DynamoDBHashKey
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
