package models.dao;

import models.adventure.Adventurer;
import models.adventure.group.AdventurerGroup;
import models.dao.common.AdventureComponentDAO;
import models.dao.common.CommonRangeEntityDAO;

import java.util.ArrayList;
import java.util.List;

public class AdventurerGroupDAO<T>  extends AdventureComponentDAO<AdventurerGroup> {

    public AdventurerGroupDAO() {
        super(AdventurerGroup.class);
    }

    public List<Adventurer> getAdventurersOfGroup(String advId, String groupId) {
        List<Adventurer> toReturn = new ArrayList<Adventurer>();
        AdventurerGroup group = get(advId,groupId);
        if(group==null)
            return toReturn;
        for(String adventurerId : group.getAdventurerIds()) {
            Adventurer adventurer= new AdventurerDAO().get(advId,adventurerId);
            toReturn.add(adventurer);
        }
        return toReturn;
    }

}
