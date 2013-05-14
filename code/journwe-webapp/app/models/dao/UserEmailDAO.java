package models.dao;

import models.UserEmail;
import models.dao.common.CommonRangeEntityDAO;

public class UserEmailDAO extends CommonRangeEntityDAO<UserEmail> {

    public UserEmailDAO() {
        super(UserEmail.class);
    }
	
}
