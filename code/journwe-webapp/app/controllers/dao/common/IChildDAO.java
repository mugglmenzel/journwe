package controllers.dao.common;

public interface IChildDAO<T> extends IDAO<T> {

	public T get(String parentId, String id);
	
	public boolean delete(String parentId, String id);
	
}
