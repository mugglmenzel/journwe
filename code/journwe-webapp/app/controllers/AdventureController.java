package controllers;

import controllers.dao.AdventureDAO;
import controllers.dao.InspirationDAO;
import models.Adventure;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.adventure.create;
import views.html.adventure.get;

import java.util.Map;

import static play.data.Form.form;

public class AdventureController extends Controller {

    private static Form<Adventure> advForm = form(Adventure.class);

    public static Result get(String catId, String insId, String id) {
        return ok(get.render(new AdventureDAO().get(insId, id), new InspirationDAO().get(catId, insId)));
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
            return ok(get.render(new AdventureDAO().get(adv.getInspirationId(), adv.getId()), new InspirationDAO().get(null, adv.getInspirationId())));
        }

    }

}
