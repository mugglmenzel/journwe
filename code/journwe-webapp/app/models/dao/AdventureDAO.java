package models.dao;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.s3.AmazonS3Client;
import com.typesafe.config.ConfigFactory;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.Comment;
import models.adventure.CommentThread;
import models.adventure.file.JournweFile;
import models.adventure.place.PlaceAdventurerPreference;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeAdventurerPreference;
import models.adventure.time.TimeOption;
import models.dao.common.CommonEntityDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdventureDAO extends CommonEntityDAO<Adventure> {

    public AdventureDAO() {
        super(Adventure.class);
    }

    public List<Adventure> allOfUserId(String userId) {
        List<Adventure> results = new ArrayList<Adventure>();
        for (Adventurer avr : new AdventurerDAO().allOfUserId(userId)) {
            results.add(get(avr.getAdventureId()));
        }
        return results;
    }

    public int countOfUserId(String userId) {
        return new AdventurerDAO().countOfUserId(userId);
    }

    public List<Adventure> allOfUserId(String userId, String lastKey, int limit) {
        List<Adventure> results = new ArrayList<Adventure>();
        for (Adventurer avr : new AdventurerDAO().allOfUserId(userId, lastKey, limit)) {
            results.add(get(avr.getAdventureId()));
        }
        return results.subList(0, results.size() >= limit ? limit : results.size());
    }

    public List<Adventure> all() {
        return pm.scan(clazz,
                new DynamoDBScanExpression());
    }


    public List<Adventure> allPublic() {
        return allPublic(null, null, -1);
    }

    public List<Adventure> allPublic(String inspirationId, String lastKey, int limit) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression().withLimit(limit);
        if (limit > 0) scan.setLimit(limit);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("id", new AttributeValue(lastKey));
            scan.setExclusiveStartKey(startkey);
        }
        if(inspirationId != null)
            scan.addFilterCondition("inspirationId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(inspirationId)));
        scan.addFilterCondition("publish", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withN("1")));
        List<Adventure> results = pm.scan(clazz,
                scan);
        return limit > 0 ? results.subList(0, results.size() >= limit ? limit : results.size()) : results;
    }


    public void deleteFull(String advId) {
        for (Adventurer advr : new AdventurerDAO().all(advId))
            new AdventurerDAO().delete(advr);

        for (models.adventure.checklist.Todo todo : new TodoDAO().all(advId))
            new TodoDAO().delete(todo);

        for (CommentThread ct : new CommentThreadDAO<Adventure>().all(advId)) {
            for (Comment c : new CommentDAO().getComments(ct.getThreadId()))
                new CommentDAO().delete(c);
            new CommentThreadDAO<Adventure>().delete(ct);
        }

        for (PlaceOption po : new PlaceOptionDAO().all(advId)) {
            for (PlaceAdventurerPreference pap : new PlaceAdventurerPreferenceDAO().all(po.getOptionId()))
                new PlaceAdventurerPreferenceDAO().delete(pap);
            new PlaceOptionDAO().delete(po);
        }

        for (TimeOption to : new TimeOptionDAO().all(advId)) {
            for (TimeAdventurerPreference tap : new TimeAdventurerPreferenceDAO().all(to.getOptionId()))
                new TimeAdventurerPreferenceDAO().delete(tap);
            new TimeOptionDAO().delete(to);
        }

        for (JournweFile jf : new JournweFileDAO().all(advId))
            new JournweFileDAO().delete(jf);

        // keep shortname to tell visitors it has been deleted
        //new AdventureShortnameDAO().delete(new AdventureShortnameDAO().getShortname(advId));

        new AdventureDAO().delete(advId);
    }

}
