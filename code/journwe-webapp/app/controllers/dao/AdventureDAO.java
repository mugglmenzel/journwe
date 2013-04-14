package controllers.dao;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import controllers.dao.common.CommonChildDAO;
import models.Adventure;
import models.Inspiration;

import java.util.List;

public class AdventureDAO extends CommonChildDAO<Adventure> {

	@Override
	public Adventure get(String parentId, String id) {
		return pm.load(Adventure.class, parentId, id);
	}

    public List<Adventure> all(int max) {
        return pm.scanPage(Adventure.class,
                new DynamoDBScanExpression().withLimit(max)).getResults();
    }

}
