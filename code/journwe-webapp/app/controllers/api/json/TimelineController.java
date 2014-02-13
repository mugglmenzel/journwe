package controllers.api.json;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.comment.Comment;
import models.adventure.comment.CommentThread;
import models.auth.SecuredUser;
import models.dao.adventure.CommentDAO;
import models.dao.adventure.CommentThreadDAO;
import models.dao.user.UserDAO;
import models.notifications.helper.AdventurerNotifier;
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
import models.adventure.file.JournweFile;
import models.dao.adventure.JournweFileDAO;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

public class TimelineController extends Controller {

	
	@Security.Authenticated(SecuredUser.class)
	public static Result get(String adventureId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        ObjectNode result;

        for(Comment c : new CommentDAO().getComments(adventureId+"_discussion")) {
            result = Json.newObject();
            result.put("type", "comment");
            result.put("comment", Json.toJson(c));
            result.put("user", Json.toJson(new UserDAO().get(c.getUserId())));
            results.add(result);
        }

        for(JournweFile f : new JournweFileDAO().all(adventureId)){
            result = Json.newObject();
            result.put("type", "file");
            result.put("file", Json.toJson(f));
            result.put("user", Json.toJson(new UserDAO().get(f.getUserId())));
            results.add(result);
        }

		return ok(Json.toJson(results));
	}

}
