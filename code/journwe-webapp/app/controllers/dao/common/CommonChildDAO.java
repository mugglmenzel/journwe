package controllers.dao.common;

public abstract class CommonChildDAO<T> extends CommonDAO<T> implements IChildDAO<T> {

	public abstract T get(String parentId, String id);

}
