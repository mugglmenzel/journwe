package controllers.dao.common;

public abstract class CommonRangeEntityDAO<T> extends CommonDAO<T> implements IChildDAO<T> {

    protected CommonRangeEntityDAO(Class<T> clazz) {
        super(clazz);
    }

    public abstract T get(String parentId, String id);

	public boolean delete(String parentId, String id) {
		try {
			pm.delete(get(parentId, id));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
}
