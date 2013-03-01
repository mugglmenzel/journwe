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
		return ok(create.render(null, catForm, new CategoryDAO().all(50)));
	}

	public static Result save() {
		Form<Category> filledCatForm = catForm.bindFromRequest();
		if (filledCatForm.hasErrors()) {
			return badRequest(create.render("Please fill out the form correctly.", filledCatForm,
					new CategoryDAO().all(50)));
		} else {
			Category cat = filledCatForm.get();
			if (new CategoryDAO().save(cat))
				return ok("Wow, Congratulations!");
			else
				return internalServerError(create.render("Something went wrong during saving :(", filledCatForm,
						new CategoryDAO().all(50)));
		}
	}

}
