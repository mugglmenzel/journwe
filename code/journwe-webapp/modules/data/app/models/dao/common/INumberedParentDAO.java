package models.dao.common;

public interface INumberedParentDAO<T> extends IDAO<T> {

	public T get(Long id);
	
	public boolean delete(Long id);
	
}
