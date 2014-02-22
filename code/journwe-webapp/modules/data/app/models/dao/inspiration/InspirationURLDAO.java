package models.dao.inspiration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.CommonEntityDAO;
import models.inspiration.InspirationTip;
import models.inspiration.InspirationURL;

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
public class InspirationURLDAO extends CommonEntityDAO<InspirationURL> {


    public InspirationURLDAO() {
        super(InspirationURL.class);
    }

    public List<InspirationURL> all(String inspirationId, String lastKey, int limit) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();

        if(limit > 0) query.withLimit(limit);

        InspirationURL tip = new InspirationURL();
        tip.setInspirationId(inspirationId);
        query.setHashKeyValues(tip);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("inspirationId", new AttributeValue(inspirationId));
            startkey.put("url", new AttributeValue(lastKey));
            query.setExclusiveStartKey(startkey);
        }

        return pm.query(InspirationURL.class, query);
    }


}
