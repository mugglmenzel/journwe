package controllers.api.json;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.typesafe.config.ConfigFactory;
import models.UserManager;
import models.adventure.Adventure;
import models.adventure.log.AdventureLogger;
import models.adventure.log.EAdventureLogSection;
import models.adventure.log.EAdventureLogTopic;
import models.adventure.log.EAdventureLogType;
import models.auth.SecuredUser;
import models.cache.CachedUserDAO;
import models.dao.NotificationDAO;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.adventure.PlaceOptionDAO;
import models.dao.adventure.TimeOptionDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserEmailDAO;
import models.notifications.ENotificationFrequency;
import models.notifications.Notification;
import models.user.User;
import models.user.UserEmail;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 26.07.13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public class UserController extends Controller {


    public static final String S3_BUCKET_PROFILE_IMAGES = "journwe-profile-images";

    private static AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey")));


    @Security.Authenticated(SecuredUser.class)
    public static Result getEmails(String userId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        for (UserEmail ue : new UserEmailDAO().getEmailsOfUser(userId)) {
            ObjectNode node = Json.newObject();
            node.put("id", ue.getUserId());
            node.put("email", ue.getEmail());
            node.put("primary", ue.isPrimary());
            node.put("validated", ue.isValidated());
            results.add(node);
        }
        return ok(Json.toJson(results));
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result setMailDigestFrequency(String userId, String frequency) {
        ENotificationFrequency freq = ENotificationFrequency.valueOf(frequency);
        if (freq != null) {
            User usr = new UserDAO().get(userId);
            usr.setNotificationDigest(freq);
            new UserDAO().save(usr);
            return ok(Json.toJson(usr.getNotificationDigest()));
        }
        return badRequest();
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getNotifications() {
        DynamicForm data = form().bindFromRequest();
        String lastId = data.get("lastId");
        int count = data.get("count") != null ? new Integer(data.get("count")).intValue() : 5;

        Logger.debug("getting notifications for " + lastId + "," + count);

        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        List<ObjectNode> results = new ArrayList<ObjectNode>();
        List<Notification> all = new NotificationDAO().all(usr.getId(), lastId, count);
        for (Notification c : all) {
            ObjectNode node = Json.newObject();

            String image = null;
            switch (c.getTopic()) {
                case ADVENTURE:
                    image = c.getTopicRef() != null ? new AdventureDAO().get(c.getTopicRef()).getImage() : "";
                    break;
                case GENERAL:
                case USER:
                default:
                    image = controllers.core.html.routes.UserController.getProfile(usr.getId()).absoluteURL(request());
                    break;
            }
            node.put("image", image);

            String link = "#";
            switch (c.getTopic()) {
                case ADVENTURE:
                    link = controllers.html.routes.AdventureController.getIndex(c.getTopicRef()).absoluteURL(request());
                    break;
                case GENERAL:
                case USER:
                default:
                    link = controllers.core.html.routes.UserController.getProfile(usr.getId()).absoluteURL(request());
                    break;
            }
            node.put("link", link);
            node.put("subject", c.getSubject());
            node.put("message", c.getMessage());
            node.put("read", c.isRead());
            node.put("sent", c.isSent());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            node.put("created", sdf.format(c.getCreated()));
            results.add(node);

            c.setRead(true);
            new NotificationDAO().save(c);
        }

        return ok(Json.toJson(results));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getPublicAdventures(String userId) {
        DynamicForm data = form().bindFromRequest();
        int count = new Integer(data.get("count")).intValue();


        List<ObjectNode> results = new ArrayList<ObjectNode>();
        for (Adventure adv : new AdventurerDAO().listAdventuresByUser(userId, null, count)) {
            if (adv.isPublish()) {
                ObjectNode node = Json.newObject();
                node.put("id", adv.getId());
                node.put("link", controllers.html.routes.AdventureController.getIndex(adv.getId()).absoluteURL(request()));
                node.put("image", adv.getImage());
                node.put("name", adv.getName());
                node.put("peopleCount", new AdventurerDAO().userCountByAdventure(adv.getId()));
                node.put("favoritePlace", adv.getFavoritePlaceId() != null ? Json.toJson(new PlaceOptionDAO().get(adv.getId(), adv.getFavoritePlaceId())) : null);
                node.put("favoriteTime", adv.getFavoriteTimeId() != null ? Json.toJson(new TimeOptionDAO().get(adv.getId(), adv.getFavoriteTimeId())) : null);
                node.put("status", new AdventurerDAO().get(adv.getId(), userId).getParticipationStatus().name());
                results.add(node);
            }
        }
        ObjectNode result = Json.newObject();
        result.put("adventures", Json.toJson(results));
        result.put("count", new AdventurerDAO().adventurePublicCountByUser(userId));

        return ok(Json.toJson(result));
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result updateImage() {
        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        if (usr != null) {
            try {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart image = body.getFiles().get(0);
                File file = image.getFile();

                if (image.getFilename() != null
                        && !"".equals(image.getFilename()) && file.length() > 0) {
                    s3.putObject(new PutObjectRequest(
                            S3_BUCKET_PROFILE_IMAGES, usr.getId(), file)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    usr.setImage(s3.getResourceUrl(S3_BUCKET_PROFILE_IMAGES,
                            usr.getId()));
                    usr.setImageTimestamp(new Long(new Date().getTime()).toString());
                }

                new CachedUserDAO().save(usr);
                new CachedUserDAO().clearCache(usr.getId());

            } catch (Exception e) {
                return badRequest();
            }
        }

        ObjectNode node = Json.newObject();
        node.put("image", usr.getImage());
        node.put("imageTimestamp", usr.getImageTimestamp());
        return ok(Json.toJson(node));
    }
}
