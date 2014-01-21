package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import models.adventure.Adventure;
import models.dao.common.AdventureComponentDAO;
import models.adventure.adventurer.Adventurer;
import models.dao.manytomany.ManyToManyCountQuery;
import models.dao.manytomany.ManyToManyListQuery;
import models.dao.queries.GSIQuery;
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

    private ManyToManyListQuery<Adventure, User> adventureToUserListQuery;
    private ManyToManyCountQuery<Adventure, User> adventureToUserCountQuery;

    public AdventurerDAO() {
        super(Adventurer.class);
        adventureToUserListQuery = new ManyToManyListQuery<Adventure, User>(Adventure.class,User.class);
        adventureToUserCountQuery = new ManyToManyCountQuery<Adventure, User>(Adventure.class,User.class);
    }

    /**
     * Did the user already create or join an adventure?
     *
     * @param userId
     * @return
     */
    public boolean isAdventurer(String userId) {
        GSIQuery q = new GSIQuery("journwe-adventurer","userId-index","userId");
        QueryResult res = q.query(userId);
        List<Map<String, AttributeValue>> items = res.getItems();
        Iterator<Map<String, AttributeValue>> itemsIter =
                items.iterator();
        // found an adventurer associated with this user?
        return (items.size()>0);
    }

    public List<Adventure> listAdventuresByUser(String userId, String lastAdventureKey, int limit) {
        return adventureToUserListQuery.listM(userId, lastAdventureKey, limit);
    }

    public List<Adventurer> listAdventurersByAdventure(String advId) {
        return all(advId);
    }

    public int userCountByAdventure(String advId) {
        return count(advId);
        //return adventureToUserCountQuery.countN(advId);
    }

    public int adventureCountByUser(String userId) {
        return adventureToUserCountQuery.countM(userId);
    }
}
