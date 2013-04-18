package controllers.dao;

import controllers.dao.common.CommonEntityDAO;
import controllers.dao.common.CommonRangeEntityDAO;
import models.User;
import models.UserEmail;

public class UserEmailDAO extends CommonRangeEntityDAO<UserEmail> {

    public UserEmailDAO() {
        super(UserEmail.class);
    }
	
}
