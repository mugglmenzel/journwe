package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.category.Category;
import models.category.CategoryHierarchy;
import models.dao.common.CommonRangeEntityDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 10.08.13
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
public class CategoryHierarchyDAO extends CommonRangeEntityDAO<CategoryHierarchy> {
    public CategoryHierarchyDAO() {
        super(CategoryHierarchy.class);
    }

    public List<CategoryHierarchy> categoryAsSuper(String catId) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        CategoryHierarchy hier = new CategoryHierarchy();
        hier.setSuperCategoryId(catId);
        query.setHashKeyValues(hier);

        return pm.query(clazz, query);
    }

    public List<CategoryHierarchy> categoryAsSub(String catId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("subCategoryId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(catId)));
        return pm.scan(clazz, scan);
    }

    public boolean isCategoryInHierarchy(String catId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("subCategoryId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(catId)));
        return pm.scan(clazz, scan).size() > 0;
    }


    public void cleanUpCategoryHierarchy() {
        for(Category cat : new CategoryDAO().all())
            if(!isCategoryInHierarchy(cat.getId())) {
                CategoryHierarchy hier = new CategoryHierarchy();
                hier.setSuperCategoryId(Category.SUPER_CATEGORY);
                hier.setSubCategoryId(cat.getId());
                save(hier);
            }
    }

}
