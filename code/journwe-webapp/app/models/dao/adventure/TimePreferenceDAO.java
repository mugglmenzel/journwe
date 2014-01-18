package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.adventurer.Adventurer;
import models.adventure.EPreferenceVote;
import models.adventure.time.TimePreference;
import models.adventure.time.TimeOption;
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
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public class TimePreferenceDAO extends CommonRangeEntityDAO<TimePreference> {

    public TimePreferenceDAO() {
        super(TimePreference.class);
    }

    public List<TimePreference> all(String timeOptionId) {
        TimePreference tap = new TimePreference();
        tap.setTimeOptionId(timeOptionId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(tap);
        return pm.query(TimePreference.class, query);
    }

    public Map<EPreferenceVote, Long> counts(String timeOptionId) {
        Map<EPreferenceVote, Long> counts = new HashMap<EPreferenceVote, Long>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            counts.put(vote, 0L);

        List<TimePreference> prefs = all(timeOptionId);
        for (TimePreference pref : prefs)
            if (pref != null) counts.put(pref.getVote(), counts.get(pref.getVote()) + 1);

        return counts;
    }

    public Map<EPreferenceVote, List<Adventurer>> adventurers(String timeOptionId) {
        Map<EPreferenceVote, List<Adventurer>> adventurers = new HashMap<EPreferenceVote, List<Adventurer>>();
        for (EPreferenceVote vote : EPreferenceVote.values())
            adventurers.put(vote, new ArrayList<Adventurer>());

        List<TimePreference> prefs = all(timeOptionId);
        for (TimePreference pref : prefs)
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
                if (advr != null) {
                    User usr = new UserDAO().get(advr.getUserId());
                    if(usr != null) adventurersNames.get(vote).add(usr.getName());
                }

        return adventurersNames;
    }
}
