package controllers;

import static play.data.Form.form;

import java.util.Map;

import controllers.dao.InspirationDAO;

import models.Adventure;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.adventure.create;

public class AdventureController extends Controller {

	private static Form<Adventure> advForm = form(Adventure.class);

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

		} else
			return ok("Wow, Congratulations!");
	}

}
