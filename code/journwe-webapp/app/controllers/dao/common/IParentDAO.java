package controllers.dao.common;

public interface IParentDAO<T> extends IDAO<T> {

	public T get(String id);
	
}
