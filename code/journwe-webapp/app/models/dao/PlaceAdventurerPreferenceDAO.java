package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.Adventurer;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.place.PlaceOption;
import models.dao.common.CommonRangeEntityDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 02.07.13
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public class PlaceAdventurerPreferenceDAO extends CommonRangeEntityDAO<PlaceAdventurerPreference> {

    public PlaceAdventurerPreferenceDAO() {
        super(PlaceAdventurerPreference.class);
    }

    public List<PlaceAdventurerPreference> all(String advId, String placeId) {
        PlaceOption po = new PlaceOption();
        po.setAdventureId(advId);
        po.setPlaceId(placeId);
        return all(advId);
    }

    public List<PlaceAdventurerPreference> all(String placeOptionId) {
        PlaceAdventurerPreference pap = new PlaceAdventurerPreference();
        pap.setPlaceOptionId(placeOptionId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(pap);
        return pm.query(PlaceAdventurerPreference.class, query);
    }

    public Map<EPreferenceVote, Long> counts(String advId, String placeId) {
        Map<EPreferenceVote, Long> counts = new HashMap<EPreferenceVote, Long>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            counts.put(vote, 0L);

        List<PlaceAdventurerPreference> prefs = all(advId, placeId);
        for (PlaceAdventurerPreference pref : prefs)
            if (pref != null) counts.put(pref.getVote(), counts.get(pref.getVote()) + 1);

        return counts;
    }

    public Map<EPreferenceVote, List<Adventurer>> adventurers(String placeOptionId) {
        PlaceOption po = PlaceOption.fromId(placeOptionId);
        return adventurers(po.getAdventureId(), po.getPlaceId());
    }

    public Map<EPreferenceVote, List<Adventurer>> adventurers(String advId, String placeId) {

        Map<EPreferenceVote, List<Adventurer>> adventurers = new HashMap<EPreferenceVote, List<Adventurer>>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            adventurers.put(vote, new ArrayList<Adventurer>());

        List<PlaceAdventurerPreference> prefs = all(advId, placeId);
        for (PlaceAdventurerPreference pref : prefs)
            if (pref != null)
                adventurers.get(pref.getVote()).add(new AdventurerDAO().get(advId, pref.getAdventurerId()));

        return adventurers;
    }

    public Map<EPreferenceVote, List<String>> adventurersNames(String advId, String placeId) {
        Map<EPreferenceVote, List<String>> adventurersNames = new HashMap<EPreferenceVote, List<String>>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            adventurersNames.put(vote, new ArrayList<String>());

        Map<EPreferenceVote, List<Adventurer>> adventurers = adventurers(advId, placeId);

        for (EPreferenceVote vote : EPreferenceVote.values())
            for (Adventurer advr : adventurers.get(vote))
                if (advr != null) adventurersNames.get(vote).add(new UserDAO().get(advr.getUserId()).getName());

        return adventurersNames;
    }

}
