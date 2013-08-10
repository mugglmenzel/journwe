package models.category;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 10.08.13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-category-hierarchy")
public class CategoryHierarchy {

    private String superCategoryId;

    private String subCategoryId;

    @DynamoDBAttribute
    public String getSuperCategoryId() {
        return superCategoryId;
    }

    public void setSuperCategoryId(String superCategoryId) {
        this.superCategoryId = superCategoryId;
    }

    @DynamoDBAttribute
    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }
}
