package controllers.api.json;

import models.adventure.comment.Comment;
import models.adventure.email.Message;
import models.adventure.file.JournweFile;
import models.auth.SecuredUser;
import models.dao.adventure.AdventureEmailMessageDAO;
import models.dao.adventure.CommentDAO;
import models.dao.adventure.JournweFileDAO;
import models.dao.user.UserDAO;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;

public class AdventureTimelineController extends Controller {


    @Security.Authenticated(SecuredUser.class)
    public static Result get(String adventureId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        ObjectNode result;

        for (Message m : new AdventureEmailMessageDAO().allNewest(adventureId)) {
            result = Json.newObject();
            result.put("type", "email");
            result.put("email", Json.toJson(m));
            results.add(result);
        }

        for (Comment c : new CommentDAO().getCommentsNewest(adventureId + "_discussion")) {
            result = Json.newObject();
            result.put("type", "comment");
            result.put("comment", Json.toJson(c));
            result.put("user", Json.toJson(new UserDAO().get(c.getUserId())));
            results.add(result);
        }

        for (JournweFile f : new JournweFileDAO().all(adventureId)) {
            result = Json.newObject();
            result.put("type", "file");
            result.put("file", Json.toJson(f));
            result.put("user", Json.toJson(new UserDAO().get(f.getUserId())));
            results.add(result);
        }

        return ok(Json.toJson(results));
    }

}
