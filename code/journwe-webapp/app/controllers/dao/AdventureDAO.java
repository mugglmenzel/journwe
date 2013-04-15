package controllers.dao;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import controllers.dao.common.CommonEntityDAO;
import controllers.dao.common.CommonRangeEntityDAO;
import models.Adventure;

import java.util.List;

public class AdventureDAO extends CommonEntityDAO<Adventure> {

    public List<Adventure> all(int max) {
        return pm.scanPage(Adventure.class,
                new DynamoDBScanExpression().withLimit(max)).getResults();
    }

}
