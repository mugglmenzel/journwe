package controllers;

import static play.data.Form.form;
import models.Category;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.category.manage;
import controllers.auth.SecuredAdminUser;
import models.dao.CategoryDAO;

public class CategoryController extends Controller {

	private static Form<Category> catForm = form(Category.class);

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result create() {
		return ok(manage.render(catForm, new CategoryDAO().all(50)));
	}

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result edit(String id) {
		Form<Category> editCatForm = catForm.fill(new CategoryDAO().get(id));
		return ok(manage.render(editCatForm, new CategoryDAO().all(50)));
	}

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result save() {
		Form<Category> filledCatForm = catForm.bindFromRequest();
		if (filledCatForm.hasErrors()) {
			flash("error", "Please fill out the form correctly.");
			return badRequest(manage.render(filledCatForm,
					new CategoryDAO().all(50)));
		} else {
			Category cat = filledCatForm.get();
			if (new CategoryDAO().save(cat)) {
				flash("success", "Saved Category.");
				return created(manage
						.render(catForm, new CategoryDAO().all(50)));
			} else {
				flash("error", "Something went wrong during saving :(");
				return internalServerError(manage.render(filledCatForm,
						new CategoryDAO().all(50)));
			}
		}
	}

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result delete(String id) {

		if (new CategoryDAO().delete(id)) {
			flash("success", "Category with id " + id + " deleted.");
			return ok(manage.render(catForm, new CategoryDAO().all(50)));
		} else {
			flash("error", "Something went wrong during deletion :(");
			return internalServerError(manage.render(catForm,
					new CategoryDAO().all(50)));
		}

	}
}
