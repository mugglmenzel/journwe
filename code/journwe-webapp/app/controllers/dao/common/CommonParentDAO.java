package controllers.dao.common;

public abstract class CommonParentDAO<T> extends CommonDAO<T> implements IParentDAO<T> {

	public abstract T get(String id);

	public boolean delete(String id) {
		try {
			pm.delete(get(id));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
