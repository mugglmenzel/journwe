package models.dao.inspiration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.CommonEntityDAO;
import models.inspiration.Inspiration;
import models.inspiration.InspirationTip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 23.10.13
 * Time: 21:51
 * To change this template use File | Settings | File Templates.
 */
public class InspirationTipDAO extends CommonEntityDAO<InspirationTip> {


    public InspirationTipDAO() {
        super(InspirationTip.class);
    }

    public List<InspirationTip> all(String inspirationId, String lastKey, int limit) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withLimit(limit);

        InspirationTip tip = new InspirationTip();
        tip.setInspirationId(inspirationId);
        query.setHashKeyValues(tip);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("inspirationId", new AttributeValue(inspirationId));
            startkey.put("created", new AttributeValue(lastKey));
            query.setExclusiveStartKey(startkey);
        }

        return pm.query(InspirationTip.class, query);
    }


    public List<InspirationTip> activated(String inspirationId, String lastKey, int limit) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withLimit(limit).withRangeKeyCondition("active", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withN("1")));

        InspirationTip tip = new InspirationTip();
        tip.setInspirationId(inspirationId);
        query.setHashKeyValues(tip);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("inspirationId", new AttributeValue(inspirationId));
            startkey.put("created", new AttributeValue(lastKey));
            query.setExclusiveStartKey(startkey);
        }

        return pm.query(InspirationTip.class, query);
    }
}
