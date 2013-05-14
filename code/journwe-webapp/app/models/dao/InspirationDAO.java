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

    public List<Inspiration> all(int max) {
        return pm.scan(Inspiration.class,
               new DynamoDBScanExpression().withLimit(max));
    }

    public List<Inspiration> all(int max, String catId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(max);
        scan.addFilterCondition("inspirationCategoryId", new Condition().withAttributeValueList(new AttributeValue(catId)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.scan(Inspiration.class, scan);
    }

    public Map<String, String> allOptionsMap(int max) {
        Map<String, String> result = new HashMap<String, String>();
        for (Inspiration in : all(max))
            result.put(in.getId(), in.getName());
        return result;
    }
}
