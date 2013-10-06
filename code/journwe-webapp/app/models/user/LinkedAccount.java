package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import models.user.User;
import play.db.ebean.Model;

import com.feth.play.module.pa.user.AuthUser;

import java.io.Serializable;

@DynamoDBTable(tableName = "journwe-linkedaccount")
public class LinkedAccount implements Serializable {

    // hash id
	public String id;

	// many to one = range id
	public User user;

	public String providerUserId;
	public String providerKey;

//	public static final Finder<Long, LinkedAccount> find = new Finder<Long, LinkedAccount>(
//			Long.class, LinkedAccount.class);
//
//	public static LinkedAccount findByProviderKey(final User user, String key) {
//		return find.where().eq("user", user).eq("providerKey", key)
//				.findUnique();
//	}

	public static LinkedAccount create(final AuthUser authUser) {
		final LinkedAccount ret = new LinkedAccount();
		ret.update(authUser);
		return ret;
	}
	
	public void update(final AuthUser authUser) {
		this.providerKey = authUser.getProvider();
		this.providerUserId = authUser.getId();
	}

	public static LinkedAccount create(final LinkedAccount acc) {
		final LinkedAccount ret = new LinkedAccount();
		ret.providerKey = acc.providerKey;
		ret.providerUserId = acc.providerUserId;

		return ret;
	}
}