package controllers.html;

import static play.data.Form.form;
import models.adventure.comment.Comment;
import models.adventure.comment.CommentThread;
import models.dao.adventure.CommentDAO;
import models.dao.adventure.CommentThreadDAO;

import models.dao.user.UserDAO;
import models.user.User;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.comment.createComment;
import views.html.comment.listThreads;

import com.feth.play.module.pa.PlayAuthenticate;

import models.auth.SecuredUser;

import java.util.ArrayList;
import java.util.List;

public class CommentController extends Controller {

	private static Form<Comment> commentForm = form(Comment.class);

	@Security.Authenticated(SecuredUser.class)
	public static Result createNewThreadAndFirstComment(String advId,
			String topicType) {
		CommentThread t = new CommentThread();
		t.setAdventureId(advId);
		t.setTopicType(topicType);
		t.setThreadId(advId,topicType);
		try {
			if (new CommentThreadDAO().save(t)) {
				return createComment(t.getThreadId());
			} else {
				throw new Exception(
						"Creating a new comment thread with thread id "
								+ t.getThreadId() + " has failed.");
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			flash("error",
					"Posting the comment has failed. Sorry. Please try again later.");
			return internalServerError();
		}
	}

	@Security.Authenticated(SecuredUser.class)
	public static Result createComment(String threadId) {
		return ok(createComment.render(commentForm, threadId));
	}

	@Security.Authenticated(SecuredUser.class)
	public static Result listCommentThreads(String adventureId) {
		return ok(listThreads.render(new CommentThreadDAO().all(adventureId)));
	}

}
