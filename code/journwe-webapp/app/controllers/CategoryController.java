package controllers;

import static play.data.Form.form;
import models.Category;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.category.manage;
import controllers.dao.CategoryDAO;

public class CategoryController extends Controller {

	private static Form<Category> catForm = form(Category.class);

	public static Result create() {
		return ok(manage.render(null, catForm, new CategoryDAO().all(50)));
	}

	public static Result edit(String id) {
		Form<Category> editCatForm = catForm.fill(new CategoryDAO().get(id));
		return ok(manage.render(null, editCatForm, new CategoryDAO().all(50)));
	}

	public static Result save() {
		Form<Category> filledCatForm = catForm.bindFromRequest();
		if (filledCatForm.hasErrors()) {
			return badRequest(manage.render(
					"Please fill out the form correctly.", filledCatForm,
					new CategoryDAO().all(50)));
		} else {
			Category cat = filledCatForm.get();
			if (new CategoryDAO().save(cat))
				return ok(manage.render("Wow, Congratulations!", catForm, new CategoryDAO().all(50)));
			else
				return internalServerError(manage.render(
						"Something went wrong during saving :(", filledCatForm,
						new CategoryDAO().all(50)));
		}
	}
}
