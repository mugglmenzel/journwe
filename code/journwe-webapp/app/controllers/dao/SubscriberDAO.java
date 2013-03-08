package controllers.dao;

import models.Subscriber;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;

import controllers.dao.common.CommonParentDAO;

public class SubscriberDAO extends CommonParentDAO<Subscriber> {

	@Override
	public Subscriber get(String id) {
		return pm.load(Subscriber.class, id, new DynamoDBMapperConfig(
				ConsistentReads.EVENTUAL));
	}

}
