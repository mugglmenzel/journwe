package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.CommonEntityDAO;
import models.Inspiration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspirationDAO extends CommonEntityDAO<Inspiration> {

    public InspirationDAO() {
        super(Inspiration.class);
    }

    public List<Inspiration> all() {
        return pm.scan(Inspiration.class,
               new DynamoDBScanExpression());
    }

    public List<Inspiration> all(String catId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("inspirationCategoryId", new Condition().withAttributeValueList(new AttributeValue(catId)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.scan(Inspiration.class, scan);
    }

    public Map<String, String> allOptionsMap() {
        Map<String, String> result = new HashMap<String, String>();
        for (Inspiration in : all())
            result.put(in.getInspirationId(), in.getName());
        return result;
    }
}
