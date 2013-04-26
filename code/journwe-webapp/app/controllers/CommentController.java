package controllers;

import static play.data.Form.form;
import models.Comment;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.imprint;
import views.html.comment.manage;
import controllers.auth.SecuredUser;
import controllers.dao.CommentDAO;

public class CommentController extends Controller {

	private static Form<Comment> commentForm = form(Comment.class);

	@Security.Authenticated(SecuredUser.class)
	public static Result index() {
		return ok(manage.render(commentForm, new CommentDAO().getCommentsByTimestamp()));
	}
	
	@Security.Authenticated(SecuredUser.class)
	public static Result create() {
		// TODO
//		return ok(manage.render(commentForm, new CommentDAO().getCommentsByTimestamp()));
		return ok(imprint.render());
	}

	@Security.Authenticated(SecuredUser.class)
	public static Result edit(String id) {
		// TODO
		return ok(imprint.render());
	}

	@Security.Authenticated(SecuredUser.class)
	public static Result save() {
		// TODO
		return ok(imprint.render());
	}

	@Security.Authenticated(SecuredUser.class)
	public static Result delete(String id) {
		// TODO
		return ok(imprint.render());
	}
}
