package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.comment.Comment;
import models.adventure.email.Message;
import models.dao.common.AdventureComponentDAO;
import models.dao.common.AdventureNumberedComponentDAO;
import models.dao.common.CommonRangeEntityDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 20.11.13
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public class AdventureEmailMessageDAO extends AdventureNumberedComponentDAO<Message> {

    public AdventureEmailMessageDAO() {
        super(Message.class);
    }

    public List<Message> allNewest(final String advId) {
        Message hashKey = new Message();
        hashKey.setAdventureId(advId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(hashKey).withScanIndexForward(false);
        return pm.query(Message.class, query);
    }

}
