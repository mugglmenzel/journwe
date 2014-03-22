package controllers.api.json;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import models.adventure.Adventure;
import models.adventure.AdventureReminder;
import models.adventure.EAdventureReminderType;
import models.adventure.log.AdventureLogger;
import models.adventure.log.EAdventureLogSection;
import models.adventure.log.EAdventureLogTopic;
import models.adventure.log.EAdventureLogType;
import models.auth.SecuredUser;
import models.authorization.AuthorizationMessage;
import models.authorization.JournweAuthorization;
import models.category.Category;
import models.dao.AdventureReminderDAO;
import models.dao.AdventureShortnameDAO;
import models.dao.adventure.AdventureDAO;
import models.dao.category.CategoryDAO;
import models.dao.manytomany.AdventureToCategoryDAO;
import models.notifications.helper.AdventurerNotifier;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.util.Date;

import static play.data.Form.form;

public class AdventureController extends Controller {

    public static final String S3_BUCKET_ADVENTURE_IMAGES = "journwe-adventure-images";

    private static final AWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey"));


    @Security.Authenticated(SecuredUser.class)
    public static Result updateImage(String advId) {
        if (!new JournweAuthorization(advId).canEditAdventureImage())
            return AuthorizationMessage.notAuthorizedResponse();
        Adventure adv = new AdventureDAO().get(advId);
        try {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart image = body.getFiles().get(0);
            File file = image.getFile();

            if (image.getFilename() != null
                    && !"".equals(image.getFilename()) && file.length() > 0) {
                AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                s3.putObject(new PutObjectRequest(
                        S3_BUCKET_ADVENTURE_IMAGES, adv.getId(), file)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                adv.setImage(s3.getResourceUrl(S3_BUCKET_ADVENTURE_IMAGES,
                        adv.getId()));
                adv.setImageTimestamp(new Long(new Date().getTime()).toString());
            }

            new AdventureDAO().save(adv);
            AdventureLogger.log(adv.getId(), EAdventureLogType.TEXT, EAdventureLogTopic.BACKGROUND_UPLOAD, EAdventureLogSection.ALL, image.getFilename());
        } catch (Exception e) {
            return badRequest();
        }

        ObjectNode node = Json.newObject();
        node.put("image", adv.getImage());
        node.put("imageTimestamp", adv.getImageTimestamp());
        return ok(Json.toJson(node));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateCategory(String advId) {
        DynamicForm data = form().bindFromRequest();
        String catId = data.get("categoryId");
        Category result = new Category();
        if (catId != null && !"".equals(catId)) {
            result = new CategoryDAO().get(catId);
            Adventure adv = new AdventureDAO().get(advId);
            // Save Adventure-to-Category relationship
            if (adv != null && result != null) {
                new AdventureToCategoryDAO().deleteAllMRelationships(adv);
                new AdventureToCategoryDAO().createManyToManyRelationship(adv, result);
                AdventureLogger.log(adv.getId(), EAdventureLogType.TEXT, EAdventureLogTopic.CATEGORY_CHANGED, EAdventureLogSection.ALL, result.getName());
            } else return badRequest();
        } else {
            result = new AdventureToCategoryDAO().listN(new AdventureDAO().get(advId).getId(), null, -1).get(0);
        }
        return ok(Json.toJson(result));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updatePublic(String advId) {
        DynamicForm data = form().bindFromRequest();
        Boolean publish = new Boolean(data.get("publish"));
        Adventure adv = new AdventureDAO().get(advId);
        adv.setPublish(publish);
        new AdventureDAO().save(adv);
        AdventureLogger.log(adv.getId(), EAdventureLogType.TEXT, EAdventureLogTopic.PUBLISH_CHANGE, EAdventureLogSection.ALL, publish.toString());

        return ok(Json.toJson(adv.isPublish()));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updatePlaceVoteOpen(String advId) {
        if (!new JournweAuthorization(advId).canChangeVoteOnOffForPlaces())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm data = form().bindFromRequest();
        Boolean openVote = new Boolean(data.get("voteOpen"));
        Adventure adv = new AdventureDAO().get(advId);
        adv.setPlaceVoteOpen(openVote);
        new AdventureDAO().save(adv);

        AdventureLogger.log(adv.getId(), EAdventureLogType.TEXT, EAdventureLogTopic.PLACE_VOTE_OPEN, EAdventureLogSection.PLACES, openVote.toString());
        new AdventurerNotifier().notifyAdventurers(advId, "The Place Vote is now " + (adv.getPlaceVoteOpen() ? "open" : "closed") + ".", "Place Vote");

        return ok(Json.toJson(adv.getPlaceVoteOpen()));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updatePlaceVoteDeadline(String advId) {
        if (!new JournweAuthorization(advId).canChangeVoteOnOffForDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm data = form().bindFromRequest();
        Long deadline = new Long(data.get("voteDeadline"));
        Adventure adv = new AdventureDAO().get(advId);
        adv.setPlaceVoteDeadline(deadline);
        new AdventureDAO().save(adv);

        AdventureReminder rem = new AdventureReminder();
        rem.setAdventureId(advId);
        rem.setType(EAdventureReminderType.PLACE);
        rem.setReminderDate(adv.getPlaceVoteDeadline() - (3 * 24 * 60 * 60));
        new AdventureReminderDAO().save(rem);

        AdventureLogger.log(adv.getId(), EAdventureLogType.TEXT, EAdventureLogTopic.PLACE_DEADLINE, EAdventureLogSection.PLACES, deadline.toString());
        new AdventurerNotifier().notifyAdventurers(advId, "Please adhere to the place voting deadline!", "Place Vote");

        return ok(Json.toJson(adv.getPlaceVoteDeadline()));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result placeVoteOpen(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        if (adv == null) return badRequest();
        else
            return ok(Json.toJson(adv.getPlaceVoteDeadline() != null ? adv.getPlaceVoteDeadline() > new Date().getTime() && adv.getPlaceVoteOpen() : adv.getPlaceVoteOpen()));
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result updateTimeVoteOpen(String advId) {
        if (!new JournweAuthorization(advId).canChangeVoteOnOffForDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm data = form().bindFromRequest();
        Boolean openVote = new Boolean(data.get("voteOpen"));
        Adventure adv = new AdventureDAO().get(advId);
        adv.setTimeVoteOpen(openVote);
        new AdventureDAO().save(adv);

        AdventureLogger.log(adv.getId(), EAdventureLogType.TEXT, EAdventureLogTopic.TIME_VOTE_OPEN, EAdventureLogSection.TIMES, openVote.toString());
        new AdventurerNotifier().notifyAdventurers(advId, "The Time Vote is now " + (adv.getTimeVoteOpen() ? "open" : "closed") + ".", "Time Vote");

        return ok(Json.toJson(adv.getTimeVoteOpen()));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateTimeVoteDeadline(String advId) {
        if (!new JournweAuthorization(advId).canChangeVoteOnOffForDateAndTime())
            return AuthorizationMessage.notAuthorizedResponse();
        DynamicForm data = form().bindFromRequest();
        Long deadline = new Long(data.get("voteDeadline"));
        Adventure adv = new AdventureDAO().get(advId);
        adv.setTimeVoteDeadline(deadline);
        new AdventureDAO().save(adv);

        AdventureReminder rem = new AdventureReminder();
        rem.setAdventureId(advId);
        rem.setType(EAdventureReminderType.TIME);
        rem.setReminderDate(adv.getPlaceVoteDeadline() - (3 * 24 * 60 * 60));
        new AdventureReminderDAO().save(rem);

        AdventureLogger.log(adv.getId(), EAdventureLogType.TEXT, EAdventureLogTopic.TIME_DEADLINE, EAdventureLogSection.TIMES, deadline.toString());
        new AdventurerNotifier().notifyAdventurers(advId, "Please adhere to the time voting deadline!", "Time Vote");

        return ok(Json.toJson(adv.getTimeVoteDeadline()));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result timeVoteOpen(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        if (adv == null) return badRequest();
        else
            return ok(Json.toJson(adv.getTimeVoteDeadline() != null ? adv.getTimeVoteDeadline() > new Date().getTime() && adv.getTimeVoteOpen() : adv.getTimeVoteOpen()));
    }


    public static Result checkShortname() {
        DynamicForm requestData = form().bindFromRequest();

        ObjectNode node = Json.newObject();
        node.put("value", requestData.get("value"));
        node.put("valid", new AdventureShortnameDAO().exists(requestData.get("value")) ? 0 : 1);
        node.put("message", "Shortname already exists!");
        Logger.info(node.toString());
        return ok(Json.toJson(node));
    }


}
