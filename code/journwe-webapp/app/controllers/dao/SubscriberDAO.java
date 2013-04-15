package controllers.dao;

import controllers.dao.common.CommonEntityDAO;
import models.Subscriber;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;

public class SubscriberDAO extends CommonEntityDAO<Subscriber> {

	@Override
	public Subscriber get(String id) {
		return pm.load(Subscriber.class, id, new DynamoDBMapperConfig(
				ConsistentReads.EVENTUAL));
	}

}
