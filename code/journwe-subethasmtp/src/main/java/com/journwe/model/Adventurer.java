package com.journwe.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 15.04.13
 * Time: 08:29
 * To change this template use File | Settings | File Templates.
 */
@DynamoDBTable(tableName = "journwe-adventurer")
public class Adventurer {

    private String adventureId;

    private String userIdRangeKey;

    private String userId;


    @DynamoDBHashKey
    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    @DynamoDBRangeKey
    public String getUserIdRangeKey() {
        return userIdRangeKey;
    }

    public void setUserIdRangeKey(String userIdRangeKey) {
        this.userIdRangeKey = userIdRangeKey;
        this.userId = userIdRangeKey;
    }

    // We need the GSI to check if a user has already created adventures
    // to identify new users who need more guidance through the website
    @DynamoDBIndexHashKey(globalSecondaryIndexName =
            "userId-index")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.userIdRangeKey = userId;
    }
}
