package controllers.html;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.Adventure;
import models.adventure.adventurer.Adventurer;
import models.adventure.adventurer.EAdventurerParticipation;
import models.auth.SecuredUser;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.manytomany.AdventureToUserDAO;
import models.dao.user.UserDAO;
import models.user.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 28.06.13
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class AdventurePeopleController extends Controller {


    //TODO: Create JSON API version of participate
    @Security.Authenticated(SecuredUser.class)
    public static Result participate(final String advId) {

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        if (advr == null) {
            advr = new Adventurer();
            advr.setUserId(usr.getId());
            advr.setAdventureId(advId);
            advr.setParticipationStatus(EAdventurerParticipation.APPLICANT);
            new AdventurerDAO().save(advr);

        } else if (EAdventurerParticipation.INVITEE.equals(advr.getParticipationStatus())) {
            advr.setParticipationStatus(EAdventurerParticipation.GOING);
            new AdventurerDAO().save(advr);
        }
        clearCache(advId);
        ApplicationController.clearUserCache(usr.getId());


        return AdventureController.getIndex(advId);
    }

    //TODO: Create JSON API version of leave
    @Security.Authenticated(SecuredUser.class)
    public static Result leave(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());

        new AdventurerDAO().delete(advr);

        clearCache(advId);
        ApplicationController.clearUserCache(usr.getId());


        flash("success", "You left the adventure " + new AdventureDAO().get(advId).getName());

        if (new AdventurerDAO().userCountByAdventure(advId) > 0)
            return redirect(controllers.html.routes.ApplicationController.index());
        else
            return AdventureController.delete(advId);
    }


    private static void clearCache(final String advId) {
        Cache.remove("adventure." + advId + ".adventurers.all");
        Cache.remove("adventure." + advId + ".adventurers.participants");
        Cache.remove("adventure." + advId + ".adventurers.invitees");
        Cache.remove("adventure." + advId + ".adventurers.applicants");
    }

}
