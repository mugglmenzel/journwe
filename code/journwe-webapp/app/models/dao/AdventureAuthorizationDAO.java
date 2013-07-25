package models.dao;

import models.adventure.AdventureAuthorization;
import models.dao.common.CommonRangeEntityDAO;

public class AdventureAuthorizationDAO extends CommonRangeEntityDAO<AdventureAuthorization> {

    public AdventureAuthorizationDAO() {
        super(AdventureAuthorization.class);
    }

}
