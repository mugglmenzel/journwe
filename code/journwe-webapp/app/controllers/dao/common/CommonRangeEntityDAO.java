package controllers.dao.common;

public abstract class CommonRangeEntityDAO<T> extends CommonDAO<T> implements IChildDAO<T> {

    protected CommonRangeEntityDAO(Class<T> clazz) {
        super(clazz);
    }

    public T get(String hashId, String rangeId) {
        return pm.load(clazz, hashId, rangeId);
    }

	public boolean delete(String parentId, String id) {
		try {
			pm.delete(get(parentId, id));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
}
