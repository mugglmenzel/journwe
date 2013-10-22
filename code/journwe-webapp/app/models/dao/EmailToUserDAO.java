package models.dao;

import models.dao.common.CommonRangeEntityDAO;
import models.user.EmailToUser;

public class EmailToUserDAO extends CommonRangeEntityDAO<EmailToUser> {

    public EmailToUserDAO() {
        super(EmailToUser.class);
    }
}
