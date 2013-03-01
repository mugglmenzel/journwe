package controllers;

import static play.data.Form.form;
import models.Category;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.category.create;
import controllers.dao.CategoryDAO;

public class CategoryController extends Controller {

	private static Form<Category> catForm = form(Category.class);

	public static Result create() {
		return ok(create.render(catForm));
	}

	public static Result save() {
		Form<Category> filledCatForm = catForm.bindFromRequest();
		if (filledCatForm.hasErrors()) {
			return badRequest(create.render(filledCatForm));
		} else {
			Category cat = filledCatForm.get();
			if (new CategoryDAO().save(cat))
				return ok("Wow, Congratulations!");
			else
				return internalServerError("Something went wrong during saving :(");
		}
	}

}
