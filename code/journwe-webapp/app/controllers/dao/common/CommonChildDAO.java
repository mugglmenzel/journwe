package controllers.dao.common;

public abstract class CommonChildDAO<T> extends CommonDAO<T> implements IChildDAO<T> {

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
