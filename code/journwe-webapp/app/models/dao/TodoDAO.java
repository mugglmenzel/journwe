package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.Todo;
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
public class TodoDAO extends CommonRangeEntityDAO<Todo> {

    public TodoDAO() {
        super(Todo.class);
    }

    public List<Todo> all(String advId) {
        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));
        return pm.scan(Todo.class, scan);
    }

    public Map<String, List<Todo>> allByUser(String advId) {

        DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(advId)).withComparisonOperator(ComparisonOperator.EQ));


        Map<String, List<Todo>> userTodos = new HashMap<String, List<Todo>>();

        for (Todo todo : pm.scan(Todo.class, scan)) {
            String user = todo.getUserId();
            if (!userTodos.containsKey(user)) {
                userTodos.put(user, new ArrayList<Todo>());
            }
            userTodos.get(user).add(todo);
        }

        return userTodos;
    }

}
