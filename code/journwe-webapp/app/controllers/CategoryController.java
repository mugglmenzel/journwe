package controllers;

import models.auth.SecuredAdminUser;
import models.category.Category;
import models.category.CategoryHierarchy;
import models.dao.CategoryDAO;
import models.dao.CategoryHierarchyDAO;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.category.manage;

import static play.data.Form.form;

public class CategoryController extends Controller {

    private static Form<Category> catForm = form(Category.class);

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result create() {
        return ok(manage.render(catForm, new CategoryDAO().all()));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result edit(String id) {
        Form<Category> editCatForm = catForm.fill(new CategoryDAO().get(id));
        return ok(manage.render(editCatForm, new CategoryDAO().all()));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result save() {
        Form<Category> filledCatForm = catForm.bindFromRequest();
        if (filledCatForm.hasErrors()) {
            flash("error", "Please fill out the form correctly.");
            return badRequest(manage.render(filledCatForm,
                    new CategoryDAO().all()));
        } else {
            Category cat = filledCatForm.get();

            if (new CategoryDAO().save(cat)) {

                if (new CategoryHierarchyDAO().isCategoryInHierarchy(cat.getId())) {
                    CategoryHierarchy hier = new CategoryHierarchy();
                    hier.setSuperCategoryId(Category.SUPER_CATEGORY);
                    hier.setSubCategoryId(cat.getId());
                    new CategoryHierarchyDAO().save(hier);
                }

                flash("success", "Saved Category.");
                return created(manage
                        .render(catForm, new CategoryDAO().all()));
            } else {
                flash("error", "Something went wrong during saving :(");
                return internalServerError(manage.render(filledCatForm,
                        new CategoryDAO().all()));
            }
        }
    }


    public static Result setSuperCategory() {
        DynamicForm df = form().bindFromRequest();
        String catId = df.get("category");
        String superCatId = df.get("superCategory");
        Logger.debug("adding " + catId + " to " + superCatId);

        for (CategoryHierarchy catHier : new CategoryHierarchyDAO().categoryAsSub(catId))
            new CategoryHierarchyDAO().delete(catHier);
        CategoryHierarchy hier = new CategoryHierarchy();
        hier.setSuperCategoryId(superCatId);
        hier.setSubCategoryId(catId);
        new CategoryHierarchyDAO().save(hier);

        return ok(Json.toJson(hier));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result delete(String id) {
        for (CategoryHierarchy catHier : new CategoryHierarchyDAO().categoryAsSub(id))
            if (catHier != null) new CategoryHierarchyDAO().delete(catHier);
        for (CategoryHierarchy catHier : new CategoryHierarchyDAO().categoryAsSuper(id))
            if (catHier != null) {
                catHier.setSuperCategoryId(Category.SUPER_CATEGORY);
                new CategoryHierarchyDAO().save(catHier);
            }

        if (new CategoryDAO().delete(id)) {
            flash("success", "Category with id " + id + " deleted.");
            return ok(manage.render(catForm, new CategoryDAO().all()));
        } else {
            flash("error", "Something went wrong during deletion :(");
            return internalServerError(manage.render(catForm,
                    new CategoryDAO().all()));
        }

    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result updateCountCache() {
        new CategoryDAO().updateCategoryCountCache();
        return ok();
    }

}
