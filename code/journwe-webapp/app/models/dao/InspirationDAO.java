package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.CommonEntityDAO;
import models.inspiration.Inspiration;

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

    public List<Inspiration> all(String lastKey, int limit) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(limit);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("id", new AttributeValue(lastKey));
            scan.setExclusiveStartKey(startkey);
        }

        return pm.scan(Inspiration.class, scan);
    }

    public Map<String, String> allOptionsMap() {
        Map<String, String> result = new HashMap<String, String>();
        for (Inspiration in : all())
            result.put(in.getId(), in.getName());
        return result;
    }
}
