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
import java.util.Collections;
import java.util.Comparator;
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
            result.put("timestamp", m.getTimestamp());
            results.add(result);
        }

        for (Comment c : new CommentDAO().getCommentsNewest(adventureId + "_discussion")) {
            result = Json.newObject();
            result.put("type", "comment");
            result.put("comment", Json.toJson(c));
            result.put("user", Json.toJson(new UserDAO().get(c.getUserId())));
            result.put("timestamp", c.getTimestamp());
            results.add(result);
        }

        for (JournweFile f : new JournweFileDAO().allNewest(adventureId)) {
            result = Json.newObject();
            result.put("type", "file");
            result.put("file", Json.toJson(f));
            result.put("user", Json.toJson(new UserDAO().get(f.getUserId())));
            result.put("timestamp", f.getTimestamp());
            results.add(result);
        }

        Collections.sort(results, new Comparator<ObjectNode>() {
            @Override
            public int compare(ObjectNode jsonNode1, ObjectNode jsonNode2) {
                return new Long(jsonNode1.get("timestamp").getLongValue()).compareTo(jsonNode2.get("timestamp").getLongValue());
            }
        });

        return ok(Json.toJson(results));
    }

}
