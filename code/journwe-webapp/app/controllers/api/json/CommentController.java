package controllers.api.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.PlayAuthenticate;
import models.UserManager;
import models.adventure.comment.Comment;
import models.auth.SecuredUser;
import models.dao.adventure.CommentDAO;
import models.dao.user.UserDAO;
import models.user.User;
import org.joda.time.DateTime;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.AdventurerNotifier;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

public class CommentController extends Controller {

    @Security.Authenticated(SecuredUser.class)
    public static Result saveComment() {
        try {
            User user = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
            if (user == null)
                throw new Exception("Posting a comment failed because user is null.");
            Form<Comment> filledCommentForm = form(Comment.class).bindFromRequest();
            Comment comment = filledCommentForm.get();
            comment.setTimestamp(new Long(DateTime.now().getMillis()));
            comment.setUserId(user.getId());
            if (new CommentDAO().save(comment)) {
                Logger.debug("comment on adventure " + comment.getAdventureId());
                new AdventurerNotifier().notifyAdventurers(comment.getAdventureId(), user.getName() + " wrote a comment.", "Comment");

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

        for (Comment c : new CommentDAO().getComments(threadId)) {
            ObjectNode result = Json.newObject();
            result.put("comment", Json.toJson(c));
            result.put("user", Json.toJson(new UserDAO().get(c.getUserId())));
            results.add(result);
        }

        return ok(Json.toJson(results));
    }

}
