package controllers;

import controllers.dao.CategoryDAO;
import controllers.dao.InspirationDAO;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class ApplicationController extends Controller {

	public static Result index() {
		return ok(index.render(new CategoryDAO().all(10),
				new InspirationDAO().all(50)));
	}

}
