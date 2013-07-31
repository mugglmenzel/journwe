package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.CommonRangeEntityDAO;
import models.notifications.ENotificationFrequency;
import models.notifications.Notification;
import models.notifications.NotificationDigestQueueItem;
import models.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 30.07.13
 * Time: 23:04
 * To change this template use File | Settings | File Templates.
 */
public class NotificationDAO extends CommonRangeEntityDAO<Notification> {

    public NotificationDAO() {
        super(Notification.class);
    }

    public List<Notification> all(String userId) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        Notification noti = new Notification();
        noti.setUserId(userId);
        query.setHashKeyValues(noti);
        return pm.query(clazz, query);
    }

    public int count(String userId) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        Notification noti = new Notification();
        noti.setUserId(userId);
        query.setHashKeyValues(noti);
        return pm.count(clazz, query);
    }

    public List<Notification> unsent(String userId) {
        List<Notification> result = new ArrayList<Notification>();
        for (Notification notification : all(userId))
            if (!notification.isSent()) result.add(notification);
        return result;
    }

    public List<Notification> sent(String userId) {
        List<Notification> result = new ArrayList<Notification>();
        for (Notification notification : all(userId))
            if (notification.isSent()) result.add(notification);
        return result;
    }




    @Override
    public boolean save(Notification obj) {
        NotificationDigestQueueItem item = new NotificationDigestQueueItem();
        item.setDigestQueueName(new UserDAO().get(obj.getUserId()).getNotificationDigest().getDigestName());
        item.setUserId(obj.getUserId());
        pm.save(item);

        return super.save(obj);
    }
}
