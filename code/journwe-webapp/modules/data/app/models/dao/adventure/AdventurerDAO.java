package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.Adventure;
import models.dao.common.AdventureComponentDAO;
import models.adventure.adventurer.Adventurer;
import models.dao.manytomany.AdventureToUserDAO;
import models.dao.manytomany.ManyToManyCountQuery;
import models.dao.manytomany.ManyToManyListQuery;
import models.dao.user.UserDAO;
import models.user.User;
import play.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 15.04.13
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class AdventurerDAO extends AdventureComponentDAO<Adventurer> {

    public AdventurerDAO() {
        super(Adventurer.class);
    }

    public boolean save(Adventurer advr) {
        // Create many-to-many Adventure-to-User relationship
        Adventure adv = new AdventureDAO().get(advr.getAdventureId());
        User usr = new UserDAO().get(advr.getUserId());
        new AdventureToUserDAO().createManyToManyRelationship(adv, usr);
        return super.save(advr);
    }

    public boolean delete(Adventurer advr) {
        // Delete many-to-many Adventure-to-User relationship
        Adventure adv = new AdventureDAO().get(advr.getAdventureId());
        User usr = new UserDAO().get(advr.getUserId());
        new AdventureToUserDAO().deleteManyToManyRelationship(adv,usr);
        return super.delete(advr);
    }

    /**
     * Did the user already create or join an adventure?
     *
     * @param userId
     * @return
     */
    public boolean isAdventurer(String userId) {
        Adventurer hashKeyObject = new Adventurer();
        hashKeyObject.setUserId(userId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withConsistentRead(false).withIndexName("userId-index").withHashKeyValues(hashKeyObject);
        return pm.count(Adventurer.class, query) > 0;
    }

    public List<Adventure> listAdventuresByUser(String userId, String lastAdventureKey, int limit) {
        return new AdventureToUserDAO().listM(userId, lastAdventureKey, limit);
    }

    public List<Adventurer> listAdventurersByAdventure(String advId) {
        return all(advId);
    }

    public int userCountByAdventure(String advId) {
        return count(advId);
        //return adventureToUserCountQuery.countN(advId);
    }

    public int adventureCountByUser(String userId) {
        return new AdventureToUserDAO().countM(userId, null);
    }

    public int adventurePublicCountByUser(String userId) {
        int count = 0;
        for(Adventure adv : new AdventureToUserDAO().listM(userId, null, -1))
            if(adv.isPublish()) count++;
        return count;
    }

}
