package controllers.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import controllers.dao.common.CommonEntityDAO;
import models.Adventure;

import java.util.List;

public class AdventureDAO extends CommonEntityDAO<Adventure> {

    public AdventureDAO() {
        super(Adventure.class);
    }

    // TODO: use index if possible
    public List<Adventure> all(int max) {
        return pm.scan(Adventure.class,
                new DynamoDBScanExpression().withLimit(max));
    }

}
