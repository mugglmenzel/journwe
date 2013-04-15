package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import controllers.dao.AdventureDAO;
import controllers.dao.AdventurerDAO;
import controllers.dao.InspirationDAO;
import models.Adventure;
import models.Adventurer;
import models.EAdventurerParticipation;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.adventure.create;
import views.html.adventure.get;

import java.util.Map;

import static play.data.Form.form;

public class AdventureController extends Controller {

    private static Form<Adventure> advForm = form(Adventure.class);

    public static Result get(String id) {
        Adventure adv = new AdventureDAO().get(id);
        return ok(get.render(adv, new InspirationDAO().get(adv.getInspirationId()), new AdventurerDAO().all(50, id)));
    }

    public static Result create() {
        Logger.info("Test");
        Map<String, String> inspireOptions = new InspirationDAO()
                .allOptionsMap(50);
        Logger.info("Created Options List");
        return ok(create.render(advForm, inspireOptions));

    }

    public static Result save() {
        Form<Adventure> filledAdvForm = advForm.bindFromRequest();
        if (filledAdvForm.hasErrors()) {
            return badRequest(create.render(filledAdvForm,
                    new InspirationDAO().allOptionsMap(50)));

        } else {
            Adventure adv = filledAdvForm.get();
            new AdventureDAO().save(adv);
            return ok(get.render(new AdventureDAO().get(adv.getId()), new InspirationDAO().get(adv.getInspirationId()), new AdventurerDAO().all(50, adv.getId())));
        }

    }

    public static Result participate(String advId) {
        User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new Adventurer();
        advr.setUserId(usr.getId());
        advr.setAdventureId(advId);
        advr.setParticipationStatus(EAdventurerParticipation.GOING);
        new AdventurerDAO().save(advr);

        Adventure adv = new AdventureDAO().get(advId);
        return ok(get.render(adv, new InspirationDAO().get(adv.getInspirationId()), new AdventurerDAO().all(50, advId)));
    }

}
