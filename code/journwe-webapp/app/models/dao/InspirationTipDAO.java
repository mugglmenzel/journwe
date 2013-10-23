package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
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

    public List<InspirationTip> all(String lastKey, int limit) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(limit);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("id", new AttributeValue(lastKey));
            scan.setExclusiveStartKey(startkey);
        }

        return pm.scan(InspirationTip.class, scan);
    }
}
