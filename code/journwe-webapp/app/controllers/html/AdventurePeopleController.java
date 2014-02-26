package controllers.html;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.AuthProvider;
import controllers.core.html.ApplicationController;
import models.UserManager;
import models.adventure.adventurer.Adventurer;
import models.adventure.adventurer.EAdventurerParticipation;
import models.auth.SecuredUser;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.user.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import models.providers.MyUsernamePasswordAuthProvider;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 28.06.13
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class AdventurePeopleController extends Controller {

    public static Result participateFacebook(final String advId) {
        return participate(advId, "facebook");
    }

    //TODO: Create JSON API version of participate
    public static Result participate(final String advId, final String provider) {

        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        if (usr != null) {
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
        } else if (provider != null && !"".equals(provider)) {
            AuthProvider prov = AuthProvider.Registry.get(provider);
            if (prov != null) return redirect(prov.getUrl());
            else if("journwe".equals(provider))
                return ok(views.html.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
        }

        flash("info", "You need to be logged in to join a JournWe.");

        return ok(views.html.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
    }

    //TODO: Create JSON API version of leave
    @Security.Authenticated(SecuredUser.class)
    public static Result leave(final String advId) {
        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());

        new AdventurerDAO().delete(advr);

        clearCache(advId);
        ApplicationController.clearUserCache(usr.getId());


        flash("success", "You left the adventure " + new AdventureDAO().get(advId).getName());

        if (new AdventurerDAO().userCountByAdventure(advId) > 0)
            return redirect(controllers.html.routes.IndexController.index());
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
