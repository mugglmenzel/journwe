package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.email.Message;
import models.dao.common.CommonRangeEntityDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 20.11.13
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public class AdventureEmailMessageDAO extends CommonRangeEntityDAO<Message> {

    public AdventureEmailMessageDAO() {
        super(Message.class);
    }

    public List<Message> all(String advId) {
        Message msg = new Message();
        msg.setAdventureId(advId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(msg);
        return pm.query(Message.class, query);
    }

}
