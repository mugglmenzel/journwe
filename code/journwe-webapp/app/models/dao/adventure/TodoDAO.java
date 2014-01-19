package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.todo.Todo;
import models.dao.common.AdventureComponentDAO;
import models.dao.common.CommonRangeEntityDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 15.04.13
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class TodoDAO extends AdventureComponentDAO<Todo> {

    public TodoDAO() {
        super(Todo.class);
    }

    public List<Todo> all(String userId, String advId) {
        List<Todo> result = allByUser(advId).get(userId);
        return result != null ? result : new ArrayList<Todo>();
    }

    public Map<String, List<Todo>> allByUser(String advId) {

        DynamoDBQueryExpression<Todo> qe = getQueryExpression(advId);
        List<Todo> result = pm.query(clazz, qe);
        Map<String, List<Todo>> userTodos = new HashMap<String, List<Todo>>();
        for (Todo todo : result) {
            String user = todo.getUserId();
            if (!userTodos.containsKey(user)) {
                userTodos.put(user, new ArrayList<Todo>());
            }
            userTodos.get(user).add(todo);
        }

        return userTodos;
    }

}
