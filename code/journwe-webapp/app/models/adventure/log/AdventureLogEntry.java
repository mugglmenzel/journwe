package models.adventure.log;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import models.adventure.IAdventureComponent;
import models.adventure.adventurer.EAdventurerParticipation;
import models.dao.helpers.EnumMarshaller;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 13.02.14
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-adventure-log-entry")
public class AdventureLogEntry implements IAdventureComponent {

    private String adventureId;

    private Long timestamp;

    private EAdventureLogType type;

    private EAdventureLogTopic topic;

    private EAdventureLogSection section;

    private String data;

    public AdventureLogEntry() {
    }

    public AdventureLogEntry(String adventureId, Long timestamp, EAdventureLogType type, EAdventureLogTopic topic, EAdventureLogSection section, String data) {
        this.adventureId = adventureId;
        this.timestamp = timestamp;
        this.type = type;
        this.topic = topic;
        this.section = section;
        this.data = data;
    }

    @DynamoDBHashKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBRangeKey
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBIndexRangeKey(localSecondaryIndexName = "type-index")
    @DynamoDBMarshalling(marshallerClass = TypeMarshaller.class)
    public EAdventureLogType getType() {
        return type;
    }

    public void setType(EAdventureLogType type) {
        this.type = type;
    }


    @DynamoDBIndexRangeKey(localSecondaryIndexName = "topic-index")
    @DynamoDBMarshalling(marshallerClass = TopicMarshaller.class)
    public EAdventureLogTopic getTopic() {
        return topic;
    }

    public void setTopic(EAdventureLogTopic topic) {
        this.topic = topic;
    }

    @DynamoDBIndexRangeKey(localSecondaryIndexName = "section-index")
    @DynamoDBMarshalling(marshallerClass = SectionMarshaller.class)
    public EAdventureLogSection getSection() {
        return section;
    }

    public void setSection(EAdventureLogSection section) {
        this.section = section;
    }

    @DynamoDBAttribute
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static class TypeMarshaller extends EnumMarshaller<EAdventureLogType> {
    }

    public static class TopicMarshaller extends EnumMarshaller<EAdventureLogTopic> {
    }

    public static class SectionMarshaller extends EnumMarshaller<EAdventureLogSection> {
    }
}
