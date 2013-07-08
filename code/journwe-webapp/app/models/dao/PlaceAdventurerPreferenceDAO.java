package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.Adventurer;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.time.TimeAdventurerPreference;
import models.dao.common.CommonRangeEntityDAO;
import play.Logger;

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


    public List<PlaceAdventurerPreference> all(String placeOptionId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("placeoptionid", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(placeOptionId)));
        return pm.scan(PlaceAdventurerPreference.class, scan);
    }

    public Map<EPreferenceVote, Long> counts(String placeOptionId) {
        Map<EPreferenceVote, Long> counts = new HashMap<EPreferenceVote, Long>();
        for(EPreferenceVote vote : EPreferenceVote.values())
            counts.put(vote, 0L);

        List<PlaceAdventurerPreference> prefs = all(placeOptionId);
        for(PlaceAdventurerPreference pref : prefs)
            counts.put(pref.getVote(), counts.get(pref.getVote())+1);

        return counts;
    }

    public Map<EPreferenceVote, List<Adventurer>> adventurers(String placeOptionId) {
        Map<EPreferenceVote, List<Adventurer>> adventurers = new HashMap<EPreferenceVote, List<Adventurer>>();
        for(EPreferenceVote vote : EPreferenceVote.values())
            adventurers.put(vote, new ArrayList<Adventurer>());

        List<PlaceAdventurerPreference> prefs = all(placeOptionId);
        for(PlaceAdventurerPreference pref : prefs)
            adventurers.get(pref.getVote()).add(new AdventurerDAO().get(new PlaceOptionDAO().get(placeOptionId).getAdventureId(), pref.getAdventurerId()));

        return adventurers;
    }

    public Map<EPreferenceVote, List<String>> adventurersNames(String placeOptionId) {
        Map<EPreferenceVote, List<String>> adventurersNames = new HashMap<EPreferenceVote, List<String>>();
        for(EPreferenceVote vote : EPreferenceVote.values())
            adventurersNames.put(vote, new ArrayList<String>());

        Map<EPreferenceVote, List<Adventurer>> adventurers = adventurers(placeOptionId);

        for(EPreferenceVote vote : EPreferenceVote.values())
        for(Adventurer advr : adventurers.get(vote))
            adventurersNames.get(vote).add(new UserDAO().get(advr.getUserId()).getName());

        return adventurersNames;
    }

}
