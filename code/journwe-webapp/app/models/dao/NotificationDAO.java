package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import models.dao.common.CommonRangeEntityDAO;
import models.dao.user.UserDAO;
import models.notifications.Notification;
import models.notifications.NotificationDigestQueueItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return all(userId, null, -1);
    }

    public List<Notification> all(String userId, String lastKey, int limit) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        query.setScanIndexForward(false);

        if (limit > 0) query.setLimit(limit);

        if (lastKey != null && !"".equals(lastKey)) {
            Map<String, AttributeValue> startkey = new HashMap<String, AttributeValue>();
            startkey.put("userId", new AttributeValue(userId));
            startkey.put("created", new AttributeValue(lastKey));
            query.setExclusiveStartKey(startkey);
        }

        Notification noti = new Notification();
        noti.setUserId(userId);
        query.setHashKeyValues(noti);

        List<Notification> results = pm.query(clazz, query);
        return limit > 0 ? results.subList(0, results.size() >= limit ? limit : results.size()) : results;
    }

    public int count(String userId) {
        DynamoDBQueryExpression query = new DynamoDBQueryExpression();
        Notification noti = new Notification();
        noti.setUserId(userId);
        query.setHashKeyValues(noti);
        return pm.count(clazz, query);
    }

    public List<Notification> unread(String userId) {
        List<Notification> result = new ArrayList<Notification>();
        for (Notification notification : all(userId))
            if (!notification.isRead()) result.add(notification);
        return result;
    }

    public int countUnread(String userId) {
        return unread(userId).size();
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
