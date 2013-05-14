package models.dao.common;


public abstract class CommonNumberedEntityDAO<T> extends CommonDAO<T> implements INumberedParentDAO<T> {

    protected CommonNumberedEntityDAO(Class<T> clazz) {
        super(clazz);
    }

    public T get(Long id) {
        return pm.load(clazz, id);
    }

    public boolean delete(Long id) {
        try {
            pm.delete(get(id));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
