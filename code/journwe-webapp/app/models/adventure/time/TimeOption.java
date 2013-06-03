package models.adventure.time;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 03.06.13
 * Time: 23:38
 * To change this template use File | Settings | File Templates.
 */
public class TimeOption {

    private String id;

    private String adventureId;

    private String name;

    private String description;

    private Date startDate;

    private Date endDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
