package models.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 14.05.13
 * Time: 21:20
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-adventureshortname")
public class AdventureShortname {

    private String shortname;

    private String adventureId;

    public AdventureShortname() {
    }

    public AdventureShortname(String shortname, String adventureId) {
        this.shortname = shortname;
        this.adventureId = adventureId;
    }

    @DynamoDBHashKey
    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    @DynamoDBAttribute
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }
}