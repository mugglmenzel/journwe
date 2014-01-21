package models.dao.adventure;

import models.adventure.time.TimeOption;
import models.dao.common.AdventureComponentDAO;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 04.06.13
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class TimeOptionDAO extends AdventureComponentDAO<TimeOption> {

    public TimeOptionDAO() {
        super(TimeOption.class);
    }

}
