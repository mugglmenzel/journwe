package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.adventurer.Adventurer;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlacePreference;
import models.adventure.place.PlaceOption;
import models.dao.adventure.AdventurerDAO;
import models.dao.common.CommonRangeEntityDAO;
import models.dao.user.UserDAO;
import models.user.User;

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
public class PlacePreferenceDAO extends CommonRangeEntityDAO<PlacePreference> {

    public PlacePreferenceDAO() {
        super(PlacePreference.class);
    }

    public List<PlacePreference> all(String placeOptionId) {
        PlacePreference pap = new PlacePreference();
        pap.setPlaceOptionId(placeOptionId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(pap);
        return pm.query(clazz, query);
    }

    public Map<EPreferenceVote, Long> counts(String placeOptionId) {
        Map<EPreferenceVote, Long> counts = new HashMap<EPreferenceVote, Long>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            counts.put(vote, 0L);

        List<PlacePreference> prefs = all(placeOptionId);
        for (PlacePreference pref : prefs)
            if (pref != null) counts.put(pref.getVote(), counts.get(pref.getVote()) + 1);

        return counts;
    }



    public Map<EPreferenceVote, List<Adventurer>> adventurers(String placeOptionId) {

        Map<EPreferenceVote, List<Adventurer>> adventurers = new HashMap<EPreferenceVote, List<Adventurer>>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            adventurers.put(vote, new ArrayList<Adventurer>());

        List<PlacePreference> prefs = all(placeOptionId);
        for (PlacePreference pref : prefs)
            if (pref != null)
                adventurers.get(pref.getVote()).add(new AdventurerDAO().get(PlaceOption.fromId(placeOptionId).getAdventureId(), pref.getUserId()));

        return adventurers;
    }

    public Map<EPreferenceVote, List<String>> adventurersNames(String placeOptionId) {
        Map<EPreferenceVote, List<String>> adventurersNames = new HashMap<EPreferenceVote, List<String>>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            adventurersNames.put(vote, new ArrayList<String>());

        Map<EPreferenceVote, List<Adventurer>> adventurers = adventurers(placeOptionId);

        for (EPreferenceVote vote : EPreferenceVote.values())
            for (Adventurer advr : adventurers.get(vote))
                if (advr != null) {
                    User usr = new UserDAO().get(advr.getUserId());
                    if(usr != null) adventurersNames.get(vote).add(usr.getName());
                }

        return adventurersNames;
    }

}
