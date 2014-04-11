package controllers.api.json;

import com.amazonaws.auth.BasicAWSCredentials;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import models.adventure.email.Message;
import models.auth.SecuredUser;
import models.dao.adventure.AdventureEmailMessageDAO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;

public class AdventureEmailController extends Controller {

    private static BasicAWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"), ConfigFactory
            .load().getString("aws.secretKey")
    );


    @Security.Authenticated(SecuredUser.class)
    public static Result listEmails(String adventureId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        for (Message m : new AdventureEmailMessageDAO().allNewest(adventureId))
            results.add(emailToSmallJSON(m));

        return ok(Json.toJson(results));
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result getEmail(String adventureId, Long timestamp) {
        try {
            return ok(emailToJSON(new AdventureEmailMessageDAO().get(adventureId, timestamp)));
        } catch (Exception e) {
            return badRequest();
        }
    }


    public static ObjectNode emailToSmallJSON(Message msg) {
        ObjectNode node = Json.newObject();
        node.put("adventureId", msg.getAdventureId());
        node.put("sender", msg.getSender());
        node.put("subject", msg.getSubject());
        node.put("body", msg.getBody().length() > 150 ? msg.getBody().substring(0, 150) : msg.getBody());
        node.put("timestamp", msg.getTimestamp());

        return node;
    }

    public static ObjectNode emailToJSON(Message msg) {
        ObjectNode node = emailToSmallJSON(msg);
        node.put("body", msg.getBody());

        return node;
    }

}
