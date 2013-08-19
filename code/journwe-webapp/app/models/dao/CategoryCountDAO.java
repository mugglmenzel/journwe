package models.dao;

import models.category.CategoryCount;
import models.dao.common.CommonEntityDAO;
import models.dao.common.CommonNumberedEntityDAO;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 19.08.13
 * Time: 01:53
 * To change this template use File | Settings | File Templates.
 */
public class CategoryCountDAO extends CommonEntityDAO<CategoryCount> {

    public CategoryCountDAO() {
        super(CategoryCount.class);
    }

}
