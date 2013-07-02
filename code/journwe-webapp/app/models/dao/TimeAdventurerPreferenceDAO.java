package models.dao;

import models.adventure.time.TimeAdventurerPreference;
import models.dao.common.CommonRangeEntityDAO;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 02.07.13
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public class TimeAdventurerPreferenceDAO extends CommonRangeEntityDAO<TimeAdventurerPreference> {

    public TimeAdventurerPreferenceDAO() {
        super(TimeAdventurerPreference.class);
    }
}
