package controllers.dao;

import controllers.dao.common.CommonEntityDAO;
import models.User;
import models.UserEmail;

public class UserEmailDAO extends CommonEntityDAO<UserEmail> {

    public UserEmailDAO() {
        super(UserEmail.class);
    }
	
}
