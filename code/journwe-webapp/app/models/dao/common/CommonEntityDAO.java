package models.dao.common;


public abstract class CommonEntityDAO<T> extends CommonDAO<T> implements IParentDAO<T> {

    protected CommonEntityDAO(Class<T> clazz) {
        super(clazz);
    }

    public T get(String id) {
        return id != null ? pm.load(clazz, id) : null;
    }

    public boolean delete(String id) {
        try {
            pm.delete(get(id));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
