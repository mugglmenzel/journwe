package controllers;

import static play.data.Form.form;
import models.Inspiration;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.inspiration.create;
import controllers.dao.CategoryDAO;
import controllers.dao.InspirationDAO;

public class InspirationController extends Controller {

	private static Form<Inspiration> insForm = form(Inspiration.class);

	public static Result get(String id) {
		return ok();
	}
	
	public static Result create() {
		return ok(create.render(insForm, new CategoryDAO().allOptionsMap(50), new InspirationDAO().all(50)));
	}

	public static Result save() {
		Form<Inspiration> filledInsForm = insForm.bindFromRequest();
		if (filledInsForm.hasErrors()) {
			return badRequest(create.render(filledInsForm, new CategoryDAO().allOptionsMap(50),
					new InspirationDAO().all(50)));
		} else {
			Inspiration cat = filledInsForm.get();
			if (new InspirationDAO().save(cat))
				return ok("Wow, Congratulations!");
			else
				return internalServerError(create.render(filledInsForm, new CategoryDAO().allOptionsMap(50),
						new InspirationDAO().all(50)));
		}
	}

}
