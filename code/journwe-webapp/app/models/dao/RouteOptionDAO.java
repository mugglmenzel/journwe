package models.dao;

import models.adventure.place.PlaceOption;
import models.adventure.route.RouteOption;
import models.adventure.time.TimeOption;
import models.dao.common.AdventureComponentDAO;

public class RouteOptionDAO extends AdventureComponentDAO<RouteOption> {

    public RouteOptionDAO() {
        super(RouteOption.class);
    }

    public PlaceOption getStartPlace(String advId, String routeId) {
        RouteOption route = get(advId,routeId);
        if(route==null)
            return null;
        String placeId = route.getStartPlaceId();
        return (new PlaceOptionDAO().get(advId,placeId));
    }

    public PlaceOption getEndPlace(String advId, String routeId) {
        RouteOption route = get(advId,routeId);
        if(route==null)
            return null;
        String placeId = route.getEndPlaceId();
        return (new PlaceOptionDAO().get(advId,placeId));
    }

    public TimeOption getTime(String advId, String routeId) {
        RouteOption route = get(advId,routeId);
        if(route==null)
            return null;
        String timeId = route.getTimeId();
        return (new TimeOptionDAO().get(advId,timeId));
    }
}
