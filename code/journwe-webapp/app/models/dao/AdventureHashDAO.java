package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.Adventure;
import models.AdventureHash;
import models.dao.common.CommonEntityDAO;
import models.dao.common.CommonNumberedEntityDAO;
import models.helpers.Hashids;

import java.util.List;

@Deprecated
public class AdventureHashDAO extends CommonNumberedEntityDAO<AdventureHash> {

    public AdventureHashDAO() {
        super(AdventureHash.class);
    }

    public Adventure getAdventureByHash(String hashid) {
        return new AdventureDAO().get(get(new Hashids().decrypt(hashid)[0]).getAdventureId());
    }

}
