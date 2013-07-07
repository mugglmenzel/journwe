package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.EPreferenceVote;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.time.TimeAdventurerPreference;
import models.dao.common.CommonRangeEntityDAO;

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

    public List<TimeAdventurerPreference> all(String placeOptionId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("timeoptionid", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(placeOptionId)));
        return pm.scan(TimeAdventurerPreference.class, scan);
    }

    public Map<EPreferenceVote, Long> counts(String placeOptionId) {
        Map<EPreferenceVote, Long> counts = new HashMap<EPreferenceVote, Long>();
        for(EPreferenceVote vote : EPreferenceVote.values())
            counts.put(vote, 0L);

        List<TimeAdventurerPreference> prefs = all(placeOptionId);
        for(TimeAdventurerPreference pref : prefs)
            counts.put(pref.getVote(), counts.get(pref.getVote())+1);

        return counts;
    }
}
