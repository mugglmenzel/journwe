package models.dao.manytomany;

import models.adventure.Adventure;
import models.category.Category;
import models.dao.manytomany.ManyToManyDAO;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 10.09.13
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class AdventureToCategoryDAO extends ManyToManyDAO<Adventure, Category> {

    public AdventureToCategoryDAO() {
        super(Adventure.class,Category.class);
    }

}
