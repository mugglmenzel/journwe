package controllers.html;

import models.auth.SecuredUser;
import models.category.Category;
import models.dao.inspiration.InspirationDAO;
import models.dao.manytomany.CategoryToInspirationDAO;
import models.inspiration.Inspiration;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index.indexNew;
import views.html.inspiration.get;

import java.util.List;

import static play.data.Form.form;

public class InspirationController extends Controller {

    public static Result get(String id) {
        Inspiration ins = new InspirationDAO().get(id);
        if (ins == null) return badRequest();
        List<Category> cats = new CategoryToInspirationDAO().listM(ins.getId(), "", 0);
        Category cat = cats != null && cats.size() > 0 ? cats.iterator().next() : null;

        return ok(get.render(ins, cat));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result createAdventure(String insId) {
        return ok(indexNew.render(new InspirationDAO().get(insId)));
    }


}
