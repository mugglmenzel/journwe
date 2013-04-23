package controllers;

import static play.data.Form.form;
import models.Comment;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import controllers.auth.SecuredAdminUser;

public class CommentController extends Controller {

	private static Form<Comment> commentForm = form(Comment.class);

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result create() {
		// TODO
//		return ok(manage.render(commentForm, new CommentDAO().getCommentsByTimestamp()));
		return null;
	}

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result edit(String id) {
		// TODO
		return null;
	}

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result save() {
		// TODO
		return null;
	}

	@Security.Authenticated(SecuredAdminUser.class)
	public static Result delete(String id) {
		// TODO
		return null;
	}
}
