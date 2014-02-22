package models.dao.common;

public abstract class CommonRangeEntityDAO<T> extends CommonDAO<T> implements IChildDAO<T> {

    protected CommonRangeEntityDAO(Class<T> clazz) {
        super(clazz);
    }

    public T get(String hashId, String rangeId) {
        return hashId != null && rangeId != null ? pm.load(clazz, hashId, rangeId) : null;
    }

    public T get(Enum hashId, String rangeId) {
        return hashId != null && rangeId != null ? pm.load(clazz, hashId, rangeId) : null;
    }

	public boolean delete(String parentId, String id) {
		try {
			pm.delete(get(parentId, id));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
}
