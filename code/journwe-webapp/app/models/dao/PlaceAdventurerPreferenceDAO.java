package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.dao.common.CommonRangeEntityDAO;
import play.Logger;

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

}
