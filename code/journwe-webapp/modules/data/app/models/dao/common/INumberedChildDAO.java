package models.dao.common;

public interface INumberedChildDAO<T> extends IDAO<T> {

	public T get(String parentId, Long id);
	
	public boolean delete(String parentId, Long id);
	
}
