package models.dao.adventure;

import models.adventure.Adventure;
import models.dao.common.AdventureComponentDAO;
import models.adventure.adventurer.Adventurer;
import models.dao.manytomany.ManyToManyCountQuery;
import models.dao.manytomany.ManyToManyListQuery;
import models.user.User;

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

    public boolean isAdventurer(String userId) {
        Adventurer hashKeyObject = new Adventurer();
        hashKeyObject.setAdventureId(userId);
        return pm.load(clazz,hashKeyObject) != null;
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
