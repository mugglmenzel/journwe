package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Category;
import models.helpers.CategoryCount;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import controllers.dao.CategoryDAO;
import controllers.dao.InspirationDAO;

public class ApplicationController extends Controller {

	public static Result index() {
		List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
		for (Category cat : new CategoryDAO().all(10))
			catCounts.add(new CategoryCount(cat, new CategoryDAO()
					.countInspirations(cat.getId())));
		return ok(index.render(catCounts, new InspirationDAO().all(50), null));
	}

	public static Result categoryIndex(String catId) {
		List<CategoryCount> catCounts = new ArrayList<CategoryCount>();
		for (Category cat : new CategoryDAO().all(10))
			catCounts.add(new CategoryCount(cat, new CategoryDAO()
					.countInspirations(cat.getId())));
		return ok(index.render(catCounts,
				new InspirationDAO().all(50, catId), catId));
	}

	public static Result oAuthDenied(String provider) {
		return ok("oAuth went wrong");
	}

	public static Result ping() {
		return ok("pong");
	}
}
