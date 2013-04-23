package controllers.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import controllers.dao.common.CommonEntityDAO;
import controllers.dao.common.CommonRangeEntityDAO;
import models.Adventure;

import java.util.List;

public class AdventureDAO extends CommonEntityDAO<Adventure> {

    public AdventureDAO() {
        super(Adventure.class);
    }

    public List<Adventure> all(int max) {
    	// TODO
//        return pm.scanPage(Adventure.class,
//                new DynamoDBScanExpression().withLimit(max)).getResults();
    	return null;
    }

}
