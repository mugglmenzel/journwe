package controllers.dao;

import models.Subscriber;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.ConsistentReads;

import controllers.dao.common.CommonEntityDAO;

public class SubscriberDAO extends CommonEntityDAO<Subscriber> {

    public SubscriberDAO() {
        super(Subscriber.class);
    }

    @Override
	public Subscriber get(String id) {
		return pm.load(Subscriber.class, id, new DynamoDBMapperConfig(
				ConsistentReads.EVENTUAL));
	}

}
