package controllers.admin.html;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.typesafe.config.ConfigFactory;
import models.auth.SecuredAdminUser;
import models.category.Category;
import models.category.CategoryHierarchy;
import models.dao.category.CategoryDAO;
import models.dao.category.CategoryHierarchyDAO;
import play.Logger;
import play.cache.Cache;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.category.manage;

import java.io.File;

import static play.data.Form.form;

public class CategoryController extends Controller {

    private static final String S3_BUCKET_CATEGORY_IMAGES = "journwe-category-images";

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
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart image = body.getFile("image");

        if (filledCatForm.hasErrors()) {
            flash("error", "Please fill out the form correctly.");
            return badRequest(manage.render(filledCatForm,
                    new CategoryDAO().all()));
        } else {
            Category cat = filledCatForm.get();

            File file = image != null ? image.getFile() : null;
            ;

            try {
                if (cat.getId() == null && !new CategoryDAO().save(cat))
                    throw new Exception();


                Logger.debug("got image files " + (image != null ? image.getFilename() : "none"));
                if (image != null && image.getFilename() != null
                        && !"".equals(image.getFilename()) && file != null && file.length() > 0) {
                    AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                            ConfigFactory.load().getString("aws.accessKey"),
                            ConfigFactory.load().getString("aws.secretKey")));
                    s3.putObject(new PutObjectRequest(
                            S3_BUCKET_CATEGORY_IMAGES, cat.getId() + "/title", file)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    cat.setImage(s3.getResourceUrl(S3_BUCKET_CATEGORY_IMAGES,
                            cat.getId() + "/title"));
                } else
                    cat.setImage(new CategoryDAO().get(cat.getId()).getImage());

                if (new CategoryDAO().save(cat)) {

                    if (!new CategoryHierarchyDAO().isCategoryInHierarchy(cat.getId())) {
                        CategoryHierarchy hier = new CategoryHierarchy();
                        hier.setSuperCategoryId(Category.SUPER_CATEGORY);
                        hier.setSubCategoryId(cat.getId());
                        new CategoryHierarchyDAO().save(hier);
                        controllers.api.json.CategoryController.clearCache();
                    }

                    flash("success", "Saved Category.");
                    return created(manage
                            .render(catForm, new CategoryDAO().all()));
                } else {
                    flash("error", "Something went wrong during saving :(");
                    return internalServerError(manage.render(filledCatForm,
                            new CategoryDAO().all()));
                }
            } catch (Exception e) {
                flash("error", "Something went wrong during saving :(");
                Logger.error("category saving went wrong", e);
                return internalServerError(views.html.category.manage.render(filledCatForm,
                        new CategoryDAO().all()));
            }
        }
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result delete(String id) {
        for (CategoryHierarchy catHier : new CategoryHierarchyDAO().categoryAsSub(id))
            if (catHier != null) {
                new CategoryHierarchyDAO().delete(catHier);
                Cache.remove("subCategoriesOf." + catHier.getSuperCategoryId());
            }
        for (CategoryHierarchy catHier : new CategoryHierarchyDAO().categoryAsSuper(id))
            if (catHier != null) {
                catHier.setSuperCategoryId(Category.SUPER_CATEGORY);
                new CategoryHierarchyDAO().save(catHier);
                controllers.api.json.CategoryController.clearCache();
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

}
