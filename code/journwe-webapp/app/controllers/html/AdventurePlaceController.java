package controllers.html;

import models.adventure.place.PlaceOption;
import models.auth.SecuredUser;
import models.authorization.AuthorizationMessage;
import models.authorization.JournweAuthorization;
import models.dao.adventure.PlaceOptionDAO;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.06.13
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class AdventurePlaceController extends Controller {

    @Security.Authenticated(SecuredUser.class)
    public static Result savePlaceEditable(String advId) {
        if (!new JournweAuthorization(advId).canEditPlaces())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm requestData = form().bindFromRequest();
        Logger.debug("request: " + requestData);

        PlaceOption place = new PlaceOptionDAO().get(advId, requestData.get("pk"));
        Logger.debug("got place " + place.getDescription());

        String name = requestData.get("name");
        if ("placeDescription".equals(name))
            place.setDescription(requestData.get("value"));

        new PlaceOptionDAO().save(place);
        Logger.debug("saved place " + place.getDescription());

        return ok();
    }

}
