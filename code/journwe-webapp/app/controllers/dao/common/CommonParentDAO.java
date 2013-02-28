package controllers.dao.common;

public abstract class CommonParentDAO<T> extends CommonDAO<T> implements IParentDAO<T> {

	public abstract T get(String id);

}
