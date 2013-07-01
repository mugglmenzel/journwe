package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import controllers.auth.SecuredAdminUser;
import models.Inspiration;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.time.TimeOption;
import models.dao.*;
import models.user.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.getTime;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.06.13
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
public class AdventureTimeController extends Controller {

    public static Form<TimeOption> timeForm = form(TimeOption.class);

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getTimes(String advId) {
        return ok(Json.toJson(new TimeOptionDAO().all(advId)));
    }

    public static Result getTime(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        Inspiration ins = new InspirationDAO().get(adv.getInspirationId());
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        return ok(getTime.render(adv, ins, advr, timeForm));
    }

    public static Result saveTime(String advId) {
        TimeOption opt = form(TimeOption.class).bindFromRequest().get();
        opt.setAdventureId(advId);
        new TimeOptionDAO().save(opt);
        return getTime(advId);
    }


}
