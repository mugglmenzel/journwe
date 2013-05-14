package models.dao;

import models.User;
import models.dao.common.CommonEntityDAO;

public class UserDAO extends CommonEntityDAO<User> {

    public UserDAO() {
        super(User.class);
    }
	
}
