package models.dao.manytomany;

import models.adventure.Adventure;
import models.user.User;

/**
 * Created by markus on 15/01/14.
 */
public class AdventureToUserDAO extends ManyToManyDAO<Adventure, User> {

    public AdventureToUserDAO() {
        super(Adventure.class,User.class);
    }
}
