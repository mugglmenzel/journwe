package controllers.dao;

import models.UserEmail;
import controllers.dao.common.CommonRangeEntityDAO;

public class UserEmailDAO extends CommonRangeEntityDAO<UserEmail> {

    public UserEmailDAO() {
        super(UserEmail.class);
    }
	
}
