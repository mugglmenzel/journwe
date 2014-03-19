package models.dao.common;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.IAdventureComponent;
import models.factory.AdventureComponentFactory;

import java.util.ArrayList;
import java.util.List;


public abstract class AdventureNumberedComponentDAO<T extends IAdventureComponent> extends CommonNumberedRangeEntityDAO<T> {

	protected static DynamoDBMapper pm = PersistenceHelper.getManager();

    protected AdventureNumberedComponentDAO(Class<T> clazz) {
        super(clazz);
    }

    public List<T> all(final String advId) {
        DynamoDBQueryExpression<T> qe = getQueryExpression(advId);
        List<T> result = pm.query(clazz, qe);
        return result != null ? result : new ArrayList<T>();
    }

    public int count(final String advId) {
        DynamoDBQueryExpression<T> qe = getQueryExpression(advId);
        return pm.count(clazz, qe);
    }

    /**
     * Helper method that prepares the QueryExpression.
     *
     * @param advId
     * @return
     */
    protected DynamoDBQueryExpression<T> getQueryExpression(final String advId) {
        T key = AdventureComponentFactory.newAdventureComponent(this.clazz);
        key.setAdventureId(advId);
        // Hash key = adventure id
        DynamoDBQueryExpression<T> qe = new DynamoDBQueryExpression<T>().withHashKeyValues(key);
        return qe;
    }

}
