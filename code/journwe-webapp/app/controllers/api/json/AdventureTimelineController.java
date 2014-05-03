package controllers.api.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.adventure.comment.Comment;
import models.adventure.email.Message;
import models.adventure.file.JournweFile;
import models.adventure.log.AdventureLogEntry;
import models.auth.SecuredUser;
import models.dao.adventure.AdventureEmailMessageDAO;
import models.dao.adventure.AdventureLogDAO;
import models.dao.adventure.CommentDAO;
import models.dao.adventure.JournweFileDAO;
import models.dao.user.UserDAO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.*;

public class AdventureTimelineController extends Controller {


    @Security.Authenticated(SecuredUser.class)
    public static Result get(String adventureId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        results.addAll(allLogNewestJSON(adventureId));
        results.addAll(allEmailNewestJSON(adventureId));
        results.addAll(allCommentNewestJSON(adventureId));
        results.addAll(allFilesNewestJSON(adventureId));

        Collections.sort(results, new Comparator<ObjectNode>() {
            @Override
            public int compare(ObjectNode jsonNode1, ObjectNode jsonNode2) {
                return new Long(jsonNode2.get("timestamp").asLong()).compareTo(jsonNode1.get("timestamp").asLong());
            }
        });

        return ok(Json.toJson(results));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getLogs(String adventureId) {
        return ok(Json.toJson(allLogNewestJSON(adventureId)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getEmails(String adventureId) {
        return ok(Json.toJson(allEmailNewestJSON(adventureId)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getComments(String adventureId) {
        return ok(Json.toJson(allCommentNewestJSON(adventureId)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getFiles(String adventureId) {
        return ok(Json.toJson(allFilesNewestJSON(adventureId)));
    }


    private static List<ObjectNode> allLogNewestJSON(String advId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        //compress
        List<AdventureLogEntry> logList = new ArrayList<AdventureLogEntry>();
        AdventureLogEntry previousLog = null;
        for (AdventureLogEntry l : new AdventureLogDAO().allNewest(advId)) {
            if(previousLog == null || !l.getTopic().equals(previousLog.getTopic()))
                logList.add(l);
            previousLog = l;
        }
        for(AdventureLogEntry l : logList){
            ObjectNode result = Json.newObject();
            result.put("type", "log");
            result.put("log", Json.toJson(l));
            result.put("user", Json.toJson(new UserDAO().get(l.getUserId())));
            result.put("timestamp", l.getTimestamp());
            results.add(result);
        }

        return results;
    }

    private static List<ObjectNode> allEmailNewestJSON(String advId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        for (Message m : new AdventureEmailMessageDAO().allNewest(advId)) {
            ObjectNode result = Json.newObject();
            result.put("type", "email");
            result.put("email", AdventureEmailController.emailToSmallJSON(m));
            result.put("timestamp", m.getTimestamp());
            results.add(result);
        }
        return results;
    }

    private static List<ObjectNode> allCommentNewestJSON(String advId) {
        List<String> threads = Arrays.asList(new String[]{"discussion", "places", "times", "adventurers", "todos"});
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        for (String thread : threads)
            for (Comment c : new CommentDAO().getCommentsNewest(advId + "_" + thread)) {
                ObjectNode result = Json.newObject();
                result.put("type", "comment");
                result.put("comment", Json.toJson(c));
                result.put("user", Json.toJson(new UserDAO().get(c.getUserId())));
                result.put("timestamp", c.getTimestamp());
                results.add(result);
            }

        Collections.sort(results, new Comparator<ObjectNode>() {
            @Override
            public int compare(ObjectNode jsonNode1, ObjectNode jsonNode2) {
                return new Long(jsonNode2.get("timestamp").asLong()).compareTo(jsonNode1.get("timestamp").asLong());
            }
        });

        return results;
    }

    private static List<ObjectNode> allFilesNewestJSON(String advId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        for (JournweFile f : new JournweFileDAO().allNewest(advId)) {
            ObjectNode result = Json.newObject();
            result.put("type", "file");
            result.put("file", Json.toJson(f));
            result.put("user", Json.toJson(new UserDAO().get(f.getUserId())));
            result.put("timestamp", f.getTimestamp());
            results.add(result);
        }
        return results;
    }

}
