package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.dao.common.CommonRangeEntityDAO;
import models.notifications.ENotificationFrequency;
import models.notifications.Notification;
import models.user.User;

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

    public List<User> getDailyDigestUsers() {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("notificationDigest", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(new User.NotificationFrequencyMarshaller().marshall(ENotificationFrequency.DAILY))));
        return pm.scan(User.class, scan);
    }

    public List<User> getWeeklyDigestUsers() {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("notificationDigest", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(new User.NotificationFrequencyMarshaller().marshall(ENotificationFrequency.WEEKLY))));
        return pm.scan(User.class, scan);
    }

}
