package controllers.dao.common;

public interface IChildDAO<T> extends IDAO<T> {

	public T get(String parentId, String id);
	
}
