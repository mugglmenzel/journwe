package controllers.dao.common;

import java.lang.reflect.ParameterizedType;

public abstract class CommonEntityDAO<T> extends CommonDAO<T> implements IParentDAO<T> {

    public T get(String id) {
        return pm.load((Class<T>) (((ParameterizedType) CommonEntityDAO.class.getGenericSuperclass()).getActualTypeArguments()[0]).getClass(), id);
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
