package models.dao.common;

public abstract class CommonNumberedRangeEntityDAO<T> extends CommonDAO<T> implements INumberedChildDAO<T> {

    protected CommonNumberedRangeEntityDAO(Class<T> clazz) {
        super(clazz);
    }

    public T get(String hashId, Long rangeId) {
        return hashId != null && rangeId != null ? pm.load(clazz, hashId, rangeId) : null;
    }

    public T get(Enum hashId, Long rangeId) {
        return hashId != null && rangeId != null ? pm.load(clazz, hashId, rangeId) : null;
    }

	public boolean delete(String parentId, Long id) {
		try {
			pm.delete(get(parentId, id));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
}
