package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.feth.play.module.pa.user.AuthUser;
import models.dao.common.CommonRangeEntityDAO;
import models.LinkedAccount;
import models.user.User;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class LinkedAccountDAO extends CommonRangeEntityDAO<LinkedAccount> {

    public LinkedAccountDAO() {
        super(LinkedAccount.class);
    }

    /**
     * Create a new LinkedAccount for authUser with the user id of oldUser.
     *
     * @param oldUser
     * @param newUser
     * @return
     */
    public LinkedAccount create(final User oldUser, final AuthUser newUser) {
        final LinkedAccount ret = new LinkedAccount();
        ret.setProviderKey(newUser.getProvider());
        ret.setSocialUserId(newUser.getId());
        ret.setUserId(oldUser.getId());
        if(!this.save(ret))
            Logger.error("Saving LinkedAcoount for old user with id " + oldUser.getId() + " and new auth user with " + newUser.getProvider() + " id " + newUser.getId() + " has failed!");
        return ret;
    }

    public List<LinkedAccount> all(String userId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("userId", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(userId)));
        return pm.scan(LinkedAccount.class, scan);
    }
//
//    public void update(final AuthUser authUser) {
//        this.providerKey = authUser.getProvider();
//        this.socialUserId = authUser.getId();
//    }
//
//    public static LinkedAccount create(final LinkedAccount acc) {
//        final LinkedAccount ret = new LinkedAccount();
//        ret.providerKey = acc.providerKey;
//        ret.socialUserId = acc.socialUserId;
//
//        return ret;
//    }
}
