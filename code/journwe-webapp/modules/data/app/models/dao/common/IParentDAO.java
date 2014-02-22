package models.dao.common;

public interface IParentDAO<T> extends IDAO<T> {

	public T get(String id);
	
	public boolean delete(String id);
	
}
