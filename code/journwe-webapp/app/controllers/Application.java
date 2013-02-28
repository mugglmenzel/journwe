package controllers;

import controllers.dao.InspirationDAO;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render("Welcome to JournWe.",
				new InspirationDAO().all(50)));
	}

	public static Result get(String id) {
		return ok(index.render("Welcome to JournWe.",
				new InspirationDAO().all(50)));
	}

}
