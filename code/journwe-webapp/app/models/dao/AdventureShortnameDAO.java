package models.dao;

import models.Adventure;
import models.AdventureHash;
import models.AdventureShortname;
import models.dao.common.CommonEntityDAO;
import models.dao.common.CommonNumberedEntityDAO;
import models.helpers.Hashids;

public class AdventureShortnameDAO extends CommonEntityDAO<AdventureShortname> {

    public AdventureShortnameDAO() {
        super(AdventureShortname.class);
    }

    public Adventure getAdventureByShortname(String shortname) {
        return new AdventureDAO().get(get(shortname).getAdventureId());
    }

}
