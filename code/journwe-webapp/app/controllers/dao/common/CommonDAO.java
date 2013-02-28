package controllers.dao.common;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;


public abstract class CommonDAO<T> implements IDAO<T> {

	protected static DynamoDBMapper pm = PersistenceHelper.getManager();

	@Override
	public boolean save(T obj) {
		try {
			pm.save(obj);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean delete(T obj) {
		pm.delete(obj);
		return true;
	}

}
