package controllers.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import controllers.dao.common.CommonEntityDAO;
import models.Inspiration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspirationDAO extends CommonEntityDAO<Inspiration> {

    public InspirationDAO() {
        super(Inspiration.class);
    }

    public List<Inspiration> all(int max) {
    	// TODO
//        return pm.scanPage(Inspiration.class,
//                new DynamoDBScanExpression().withLimit(max)).getResults();
    	return null;
    }

    public List<Inspiration> all(int max, String catId) {
    	// TODO
//        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(max);
//        scan.addFilterCondition("inspirationCategoryId", new Condition().withAttributeValueList(new AttributeValue(catId)).withComparisonOperator(ComparisonOperator.EQ));
//        return pm.scanPage(Inspiration.class, scan).getResults();
    	return null;
    }

    public Map<String, String> allOptionsMap(int max) {
        Map<String, String> result = new HashMap<String, String>();
        for (Inspiration in : all(max))
            result.put(in.getId(), in.getName());
        return result;
    }
}
