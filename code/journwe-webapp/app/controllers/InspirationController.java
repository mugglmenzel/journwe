package controllers;

import static play.data.Form.form;
import models.Inspiration;
import models.Category;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.inspiration.create;
import views.html.inspiration.get;
import controllers.dao.CategoryDAO;
import controllers.dao.InspirationDAO;

public class InspirationController extends Controller {

	private static Form<Inspiration> insForm = form(Inspiration.class);

	public static Result get(String id) {
		Inspiration ins = new InspirationDAO().get("c36949fb-68bb-456a-a168-10082fc2d392", id);
		Category cat = new CategoryDAO().get(ins.getInspirationCategoryId());
		return ok(get.render(ins, cat));
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
