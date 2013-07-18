package models.dao.common;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import models.AdventureComponentFactory;
import models.adventure.IAdventureComponent;
import models.dao.common.CommonDAO;
import models.dao.common.CommonRangeEntityDAO;
import models.dao.common.PersistenceHelper;

import java.util.ArrayList;
import java.util.List;


public abstract class AdventureComponentDAO<T extends IAdventureComponent> extends CommonRangeEntityDAO<T> {

	protected static DynamoDBMapper pm = PersistenceHelper.getManager();

    protected Class<T> clazz;

    protected AdventureComponentDAO(Class<T> clazz) {
        super(clazz);
    }

    public List<T> all(final String adventureId) {
        T key = AdventureComponentFactory.newAdventureComponent(clazz);
        key.setAdventureId(adventureId);
        // Hash key = adventure id
        DynamoDBQueryExpression<T> qe = new DynamoDBQueryExpression<T>().withHashKeyValues(key);
        PaginatedQueryList<T> result = pm.query(clazz, qe);
        if(result != null)	{
            // return the results
            return result;
        } else {
            // ... else: return an empty list
            return new ArrayList<T>();
        }
    }

}
