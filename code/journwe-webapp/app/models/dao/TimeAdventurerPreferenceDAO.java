package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.Adventurer;
import models.adventure.EPreferenceVote;
import models.adventure.time.TimeAdventurerPreference;
import models.adventure.time.TimeOption;
import models.dao.common.CommonRangeEntityDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 02.07.13
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public class TimeAdventurerPreferenceDAO extends CommonRangeEntityDAO<TimeAdventurerPreference> {

    public TimeAdventurerPreferenceDAO() {
        super(TimeAdventurerPreference.class);
    }

    public List<TimeAdventurerPreference> all(String timeOptionId) {
        TimeAdventurerPreference tap = new TimeAdventurerPreference();
        tap.setTimeOptionId(timeOptionId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(tap);
        return pm.query(TimeAdventurerPreference.class, query);
    }

    public Map<EPreferenceVote, Long> counts(String timeOptionId) {
        Map<EPreferenceVote, Long> counts = new HashMap<EPreferenceVote, Long>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            counts.put(vote, 0L);

        List<TimeAdventurerPreference> prefs = all(timeOptionId);
        for (TimeAdventurerPreference pref : prefs)
            if (pref != null) counts.put(pref.getVote(), counts.get(pref.getVote()) + 1);

        return counts;
    }

    public Map<EPreferenceVote, List<Adventurer>> adventurers(String timeOptionId) {
        Map<EPreferenceVote, List<Adventurer>> adventurers = new HashMap<EPreferenceVote, List<Adventurer>>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            adventurers.put(vote, new ArrayList<Adventurer>());

        List<TimeAdventurerPreference> prefs = all(timeOptionId);
        for (TimeAdventurerPreference pref : prefs)
            if (pref != null)
                adventurers.get(pref.getVote()).add(new AdventurerDAO().get(TimeOption.fromId(timeOptionId).getAdventureId(), pref.getAdventurerId()));

        return adventurers;
    }

    public Map<EPreferenceVote, List<String>> adventurersNames(String timeOptionId) {
        Map<EPreferenceVote, List<String>> adventurersNames = new HashMap<EPreferenceVote, List<String>>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            adventurersNames.put(vote, new ArrayList<String>());

        Map<EPreferenceVote, List<Adventurer>> adventurers = adventurers(timeOptionId);

        for (EPreferenceVote vote : EPreferenceVote.values())
            for (Adventurer advr : adventurers.get(vote))
                if (advr != null) adventurersNames.get(vote).add(new UserDAO().get(advr.getUserId()).getName());

        return adventurersNames;
    }
}
