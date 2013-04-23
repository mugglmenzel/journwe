package controllers.dao;

import models.User;
import controllers.dao.common.CommonEntityDAO;

public class UserDAO extends CommonEntityDAO<User> {

    public UserDAO() {
        super(User.class);
    }
	
}
