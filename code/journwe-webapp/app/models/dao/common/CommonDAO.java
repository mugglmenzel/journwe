package models.dao.common;

import play.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


public abstract class CommonDAO<T> implements IDAO<T> {

	protected static DynamoDBMapper pm = PersistenceHelper.getManager();

    protected Class<T> clazz;

    protected CommonDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
	public boolean save(T obj) {
		try {
			pm.save(obj);
		} catch (Exception e) {
			Logger.error("Not saved " + obj, e);
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
