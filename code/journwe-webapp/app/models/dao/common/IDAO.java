package models.dao.common;

public interface IDAO<T> {

	public boolean save(T obj);
	
	public boolean delete(T obj);
	
}
