package controllers;

import static play.data.Form.form;
import models.adventure.Comment;
import models.adventure.CommentThread;
import models.dao.CommentDAO;
import models.dao.CommentThreadDAO;

import models.dao.UserDAO;
import models.user.User;
import org.codehaus.jackson.JsonNode;
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
import com.feth.play.module.pa.user.AuthUser;

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
	public static Result saveComment() {
		try {
            User user = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
            if(user==null)
                throw new Exception("Posting a comment failed because user is null.");
            Form<Comment> filledCommentForm = commentForm.bindFromRequest();
            Comment comment = filledCommentForm.get();
            comment.setTimestamp(new Long(DateTime.now().getMillis()));
            comment.setUserId(user.getId());
			if (new CommentDAO().save(comment)) {
                ObjectNode result = Json.newObject();
                result.put("comment", Json.toJson(comment));
                result.put("user", Json.toJson(new UserDAO().get(comment.getUserId())));
				return created(Json.toJson(result));
			} else {
				throw new Exception("Saving comment failed.");
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			return internalServerError();
		}
	}
	
	@Security.Authenticated(SecuredUser.class)
	public static Result listComments(String threadId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();

        for(Comment c : new CommentDAO().getComments(threadId)) {
            ObjectNode result = Json.newObject();
            result.put("comment", Json.toJson(c));
            result.put("user", Json.toJson(new UserDAO().get(c.getUserId())));
            results.add(result);
        }

		return ok(Json.toJson(results));
	}
	
	@Security.Authenticated(SecuredUser.class)
	public static Result listCommentThreads(String adventureId) {
		return ok(listThreads.render(new CommentThreadDAO().all(adventureId)));
	}

}
