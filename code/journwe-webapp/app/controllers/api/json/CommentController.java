package controllers.api.json;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.comment.Comment;
import models.adventure.comment.CommentThread;
import models.auth.SecuredUser;
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

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

public class CommentController extends Controller {

	@Security.Authenticated(SecuredUser.class)
	public static Result saveComment() {
		try {
            User user = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
            if(user==null)
                throw new Exception("Posting a comment failed because user is null.");
            Form<Comment> filledCommentForm = form(Comment.class).bindFromRequest();
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

}
