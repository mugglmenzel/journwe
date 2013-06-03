package models.dao;

import models.adventure.Adventure;
import models.adventure.AdventureHash;
import models.dao.common.CommonNumberedEntityDAO;
import models.helpers.Hashids;

@Deprecated
public class AdventureHashDAO extends CommonNumberedEntityDAO<AdventureHash> {

    public AdventureHashDAO() {
        super(AdventureHash.class);
    }

    public Adventure getAdventureByHash(String hashid) {
        return new AdventureDAO().get(get(new Hashids().decrypt(hashid)[0]).getAdventureId());
    }

}
