package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.log.AdventureLogEntry;
import models.adventure.log.EAdventureLogSection;
import models.adventure.log.EAdventureLogTopic;
import models.adventure.log.EAdventureLogType;
import models.dao.common.AdventureComponentDAO;
import models.factory.AdventureComponentFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 13.02.14
 * Time: 22:47
 * To change this template use File | Settings | File Templates.
 */
public class AdventureLogDAO extends AdventureComponentDAO<AdventureLogEntry> {

    public AdventureLogDAO() {
        super(AdventureLogEntry.class);
    }


    public List<AdventureLogEntry> allNewest(String advId) {
        AdventureLogEntry key = new AdventureLogEntry();
        key.setAdventureId(advId);

        return pm.query(AdventureLogEntry.class, new DynamoDBQueryExpression<AdventureLogEntry>().withHashKeyValues(key).withScanIndexForward(false));
    }


    public List<AdventureLogEntry> allByType(String advId, EAdventureLogType type) {
        AdventureLogEntry key = new AdventureLogEntry();
        key.setAdventureId(advId);

        return pm.query(AdventureLogEntry.class, new DynamoDBQueryExpression<AdventureLogEntry>().withHashKeyValues(key).withIndexName("type-index").withRangeKeyCondition("type", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(type.name()))));
    }

    public List<AdventureLogEntry> allByTopic(String advId, EAdventureLogTopic topic) {
        AdventureLogEntry key = new AdventureLogEntry();
        key.setAdventureId(advId);

        return pm.query(AdventureLogEntry.class, new DynamoDBQueryExpression<AdventureLogEntry>().withHashKeyValues(key).withIndexName("topic-index").withRangeKeyCondition("topic", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(topic.name()))));
    }

    public List<AdventureLogEntry> allByTopic(String advId, EAdventureLogSection section) {
        AdventureLogEntry key = new AdventureLogEntry();
        key.setAdventureId(advId);

        return pm.query(AdventureLogEntry.class, new DynamoDBQueryExpression<AdventureLogEntry>().withHashKeyValues(key).withIndexName("section-index").withRangeKeyCondition("section", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(section.name()))));
    }
}
