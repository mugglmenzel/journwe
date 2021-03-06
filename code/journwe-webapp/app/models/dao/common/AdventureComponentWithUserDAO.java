package models.dao.common;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.IAdventureComponent;
import models.adventure.IAdventureComponentWithUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdventureComponentWithUserDAO<T extends IAdventureComponentWithUser> extends AdventureComponentDAO<T> {

    protected AdventureComponentWithUserDAO(Class<T> clazz) {
        super(clazz);
    }

    public Map<String, List<T>> allByUser(String advId) {

        DynamoDBQueryExpression<T> qe = getQueryExpression(advId);
        List<T> result = pm.query(clazz, qe);

        Map<String, List<T>> userAndObjects = new HashMap<String, List<T>>();

        for (T obj : result) {
            String user = obj.getUserId();
            if (!userAndObjects.containsKey(user)) {
                userAndObjects.put(user, new ArrayList<T>());
            }
            userAndObjects.get(user).add(obj);
        }

        return userAndObjects;
    }
}
