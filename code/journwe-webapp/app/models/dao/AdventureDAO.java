package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.adventure.Adventurer;
import models.dao.common.CommonEntityDAO;
import models.adventure.Adventure;

import java.util.ArrayList;
import java.util.List;

public class AdventureDAO extends CommonEntityDAO<Adventure> {

    public AdventureDAO() {
        super(Adventure.class);
    }

    public List<Adventure> allOfUserId(String userId) {
        List<Adventure> result = new ArrayList<Adventure>();
        for(Adventurer avr : new AdventurerDAO().allOfUserId(userId)) {
            result.add(get(avr.getAdventureId()));
        }
        return result;
    }

    // TODO: use index if possible
    public List<Adventure> all(int max) {
        return pm.scan(Adventure.class,
                new DynamoDBScanExpression().withLimit(max));
    }

}
