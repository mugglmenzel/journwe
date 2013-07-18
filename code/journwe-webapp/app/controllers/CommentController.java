package controllers;

import static play.data.Form.form;
import models.adventure.Comment;
import models.adventure.CommentThread;
import models.dao.CommentDAO;
import models.dao.CommentThreadDAO;

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
import com.feth.play.module.pa.user.AuthUser;

import models.auth.SecuredUser;

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
	public static Result saveComment() {
		try {
		AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
		if(usr==null)
			throw new Exception("Posting a comment failed because user is null.");
		Form<Comment> filledCommentForm = commentForm.bindFromRequest();
		Comment comment = filledCommentForm.get();
		comment.setTimestamp(new Long(DateTime.now().getMillis()));
		comment.setUserId(usr.getId());
		Logger.debug("thread with id: "+comment.getThreadId());
		Logger.debug("ts: "+comment.getTimestamp());
			if (new CommentDAO().save(comment)) {
				flash("Posted new comment.");
				return created(Json.toJson(comment));
			} else {
				throw new Exception("Saving comment failed.");
			}
		} catch (Exception e) {
			flash("error", "Something went wrong during saving the comment.");
			Logger.error(e.getMessage());
			return internalServerError();
		}
	}
	
	@Security.Authenticated(SecuredUser.class)
	public static Result listComments(String threadId) {
		return ok(Json.toJson(new CommentDAO().getComments(threadId)));
	}
	
	@Security.Authenticated(SecuredUser.class)
	public static Result listCommentThreads(String adventureId) {
		return ok(listThreads.render(new CommentThreadDAO().all(adventureId)));
	}

}
