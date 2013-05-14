package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.Adventure;
import models.AdventureHash;
import models.dao.common.CommonEntityDAO;
import models.dao.common.CommonNumberedEntityDAO;

import java.util.List;

public class AdventureHashDAO extends CommonNumberedEntityDAO<AdventureHash> {

    public AdventureHashDAO() {
        super(AdventureHash.class);
    }


}
