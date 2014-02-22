package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.dao.common.CommonRangeEntityDAO;
import models.dao.user.UserDAO;
import models.notifications.ENotificationFrequency;
import models.notifications.NotificationDigestQueueItem;
import models.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 31.07.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class NotificationDigestQueueDAO extends CommonRangeEntityDAO<NotificationDigestQueueItem> {

    public NotificationDigestQueueDAO() {
        super(NotificationDigestQueueItem.class);
    }

    public List<User> getDigestUsers(ENotificationFrequency frequency) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        NotificationDigestQueueItem item = new NotificationDigestQueueItem();
        item.setDigestQueueName(frequency.getDigestName());
        query.setHashKeyValues(item);

        List<User> results = new ArrayList<User>();
        for(NotificationDigestQueueItem queueItem : (List<NotificationDigestQueueItem>) pm.query(clazz, query))
            results.add(new UserDAO().get(queueItem.getUserId()));

        return results;
    }
}
