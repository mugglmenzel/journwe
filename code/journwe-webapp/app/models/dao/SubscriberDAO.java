package models.dao;

import models.Subscriber;

import models.dao.common.CommonEntityDAO;

public class SubscriberDAO extends CommonEntityDAO<Subscriber> {

    public SubscriberDAO() {
        super(Subscriber.class);
    }

}
