package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.Category;
import models.Subscriber;
import models.helpers.CategoryCount;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.subscribe;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import controllers.auth.SecuredAdminUser;
import controllers.dao.CategoryDAO;
import controllers.dao.InspirationDAO;
import controllers.dao.SubscriberDAO;

public class ApplicationController extends Controller {

	private static Form<Subscriber> subForm = form(Subscriber.class);

	public static Result index() {
		AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
		if (PlayAuthenticate.isLoggedIn(Http.Context.current().session())
				&& SecuredAdminUser.isAdmin(usr)) {
			List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
			for (Category cat : new CategoryDAO().all(10))
				catCounts.add(new CategoryCount(cat, new CategoryDAO()
						.countInspirations(cat.getId())));
			return ok(index.render(catCounts, new InspirationDAO().all(50),
					null));
		} else {
			return ok(subscribe.render(subForm));
		}
	}

	public static Result subscribe() {
		Form<Subscriber> filledSubForm = subForm.bindFromRequest();
		Subscriber sub = filledSubForm.get();
		if (new SubscriberDAO().save(sub))
			flash("success", "You are subscribed now! We'll let you know.");
		else
			flash("error", "You could not be subscribed :(");
		return ok(subscribe.render(subForm));
	}

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result categoryIndex(String catId) {
		List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
		for (Category cat : new CategoryDAO().all(10))
			catCounts.add(new CategoryCount(cat, new CategoryDAO()
					.countInspirations(cat.getId())));
		return ok(index.render(catCounts, new InspirationDAO().all(50, catId),
				catId));
	}

	public static Result oAuthDenied(String provider) {
		return ok("oAuth went wrong");
	}

	public static Result ping() {
		return ok("pong");
	}
}
