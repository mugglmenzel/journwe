package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.rosaloves.bitlyj.Jmp;
import com.typesafe.config.ConfigFactory;
import models.Inspiration;
import models.adventure.*;
import models.adventure.group.AdventurerGroup;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.auth.SecuredBetaUser;
import models.authorization.JournweAuthorization;
import models.dao.*;
import models.helpers.JournweFacebookChatClient;
import models.helpers.JournweFacebookClient;
import models.notifications.helper.AdventurerNotifier;
import models.user.User;
import models.user.UserEmail;
import models.user.UserSocial;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.create;
import views.html.adventure.created;
import views.html.adventure.getIndex;
import views.html.adventure.getPublic;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;

import static com.rosaloves.bitlyj.Bitly.shorten;
import static play.data.Form.form;

public class AdventureController extends Controller {

    private static final String S3_BUCKET_ADVENTURE_IMAGES = "journwe-adventure-images";

    private static DynamicForm advForm = form();


    public static Result getIndex(String id) {
        Adventure adv = new AdventureDAO().get(id);
        if (adv == null) return badRequest();
        else {
            User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
            Adventurer advr = usr != null ? new AdventurerDAO().get(id, usr.getId()) : null;
            Inspiration ins = Inspiration.fromId(adv.getInspirationId());
            ins = ins != null ? new InspirationDAO().get(ins.getCategoryId(), ins.getInspirationId()) : ins;
            if (advr == null)
                return ok(getPublic.render(adv, ins));
            else
                return ok(getIndex.render(adv, ins, advr, AdventureTimeController.timeForm, AdventureFileController.fileForm));
        }
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getIndexShortname(String shortname) {
        return getIndex(new AdventureShortnameDAO().get(shortname).getAdventureId());
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result create() {
        return createFromInspiration(null);
    }

    //TODO: adapt to composite inspiration id
    @Security.Authenticated(SecuredBetaUser.class)
    public static Result createFromInspiration(String insId) {
        Map<String, String> inspireOptions = new InspirationDAO()
                .allOptionsMap();
        Form<Adventure> advFilledForm = form(Adventure.class);
        if (insId != null && !"".equals(insId)) {
            Adventure adv = new Adventure();
            adv.setInspirationId(insId);
            advFilledForm = advFilledForm.fill(adv);
        } else advFilledForm = advFilledForm.fill(new Adventure());
        return ok(create.render(form().fill(advFilledForm.data()), inspireOptions));

    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result saveOld() {
        Form<Adventure> filledAdvForm = form(Adventure.class).bindFromRequest();
        DynamicForm filledForm = advForm.bindFromRequest();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart image = body.getFile("image");

        String shortname = filledForm.data().get("shortname");

        if (filledAdvForm.hasErrors()) {
            flash("error", "Please complete the form.");

        } else if (shortname.length() < 3) {
            flash("error", "Shortname must be at least 3 letters.");

        } else if (new AdventureShortnameDAO().exists(shortname)) {
            flash("error", "Shortname already exists.");

        } else {
            Adventure adv = filledAdvForm.get();
            File file = image.getFile();

            try {
                if (!new AdventureDAO().save(adv))
                    throw new Exception();

                adv = new AdventureDAO().get(adv.getId());
                Inspiration ins = Inspiration.fromId(adv.getInspirationId());
                ins = ins != null ? new InspirationDAO().get(ins.getCategoryId(), ins.getInspirationId()) : ins;

                Logger.info("got image files " + image.getFilename());

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
                } else
                    adv.setImage(ins.getImage());


                if (new AdventureDAO().save(adv)) {
                    AdventureShortname advShortname = new AdventureShortname(shortname, adv.getId());
                    new AdventureShortnameDAO().save(advShortname);

                    String shortURL = routes.AdventureController.getIndexShortname(advShortname.getShortname()).absoluteURL(request());
                    try {
                        shortURL = request().host().contains("localhost") ?
                                routes.AdventureController.getIndexShortname(advShortname.getShortname()).absoluteURL(request()) :
                                Jmp.as(ConfigFactory.load().getString("bitly.username"), ConfigFactory.load().getString("bitly.apiKey")).call(shorten(routes.AdventureController.getIndexShortname(advShortname.getShortname()).absoluteURL(request()))).getShortUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flash("success",
                            "Saved Adventure with image " + adv.getImage()
                                    + ".");

                    User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

                    Adventurer advr = new Adventurer();
                    advr.setAdventureId(adv.getId());
                    advr.setUserId(usr.getId());
                    advr.setParticipationStatus(EAdventurerParticipation.GOING);
                    new AdventurerDAO().save(advr);

                    try {
                        UserEmail primaryEmail = new UserEmailDAO().getPrimaryEmailOfUser(usr.getId());
                        if (primaryEmail != null) {
                            AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                                    ConfigFactory.load().getString("aws.accessKey"),
                                    ConfigFactory.load().getString("aws.secretKey")));
                            Logger.info("got primary email: " + primaryEmail);
                            ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(primaryEmail.getEmail())).withMessage(new Message().withSubject(new Content().withData("Your new Adventure " + adv.getName())).withBody(new Body().withText(new Content().withData("Hey, We created the adventure " + adv.getName() + " for you! Share it with " + shortURL + ". The adventure's email address is " + advShortname.getShortname() + "@adventure.journwe.com.")))).withSource(advShortname.getShortname() + "@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return ok(created.render(new AdventureDAO().get(adv.getId()), ins, shortURL, advShortname.getShortname()));

                } else
                    throw new Exception();
            } catch (Exception e) {
                new AdventureDAO().delete(adv);
                AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                s3.deleteObject(S3_BUCKET_ADVENTURE_IMAGES, adv.getId());
                new AdventureShortnameDAO().delete(filledAdvForm.data().get("shortname"));

                flash("error", "Something went wrong during saving :(");
                e.printStackTrace();
                return internalServerError(create.render(filledForm,
                        new InspirationDAO()
                                .allOptionsMap()));
            }
        }
        return badRequest(create.render(filledForm,
                new InspirationDAO().allOptionsMap()));


    }


    public static Result save() {
        DynamicForm filledForm = advForm.bindFromRequest();

        Adventure adv = new Adventure();
        adv.setName(filledForm.get("name"));
        new AdventureDAO().save(adv);

        String shortURL = routes.AdventureController.getIndex(adv.getId()).absoluteURL(request());

        String shortname = filledForm.get("shortname");
        if (shortname != null) {
            AdventureShortname advShortname = new AdventureShortname(shortname, adv.getId());
            new AdventureShortnameDAO().save(advShortname);
            shortURL = routes.AdventureController.getIndexShortname(advShortname.getShortname()).absoluteURL(request());
        }


        try {
            shortURL = request().host().contains("localhost") ?
                    shortURL :
                    Jmp.as(ConfigFactory.load().getString("bitly.username"), ConfigFactory.load().getString("bitly.apiKey")).call(shorten(shortURL)).getShortUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        UserSocial us = new UserSocialDAO().findByUserId("facebook", usr.getId());

        // ADVENTURER
        Adventurer advr = new Adventurer();
        advr.setAdventureId(adv.getId());
        advr.setUserId(usr.getId());
        advr.setParticipationStatus(EAdventurerParticipation.GOING);
        new AdventurerDAO().save(advr);

        // ADVENTURER GROUP
        AdventurerGroup group = AdventurerGroupController.createEmptyGroup(adv.getId());
        new AdventurerGroupDAO().save(group);

        // PLACE AND TIME

        //List<PlaceOption> placeOptions = new ArrayList<PlaceOption>();
        //List<TimeOption> timeOptions = new ArrayList<TimeOption>();
        int placeI = 0;
        String lastPlaceId = null;
        int timeI = 0;
        String lastTimeId = null;
        for (String key : filledForm.data().keySet()) {
            if (key.startsWith("place[")) {
                PlaceOption po = new PlaceOption();
                po.setAdventureId(adv.getId());
                placeI++;
                po.setAddress(filledForm.data().get(key));
                new PlaceOptionDAO().save(po);
                lastPlaceId = po.getPlaceId();
                //placeOptions.add(po);
            }
            if (key.startsWith("time[")) {
                try {
                    TimeOption to = new TimeOption();
                    to.setAdventureId(adv.getId());
                    timeI++;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    to.setStartDate(sdf.parse(filledForm.data().get(key).split(",")[0]));
                    to.setEndDate(sdf.parse(filledForm.data().get(key).split(",")[1]));

                    new TimeOptionDAO().save(to);
                    lastTimeId = to.getTimeId();
                    //timeOptions.add(to);

                } catch (Exception e) {
                    timeI--;
                }
            }
            if (key.startsWith("email[")) {
                try {
                    String email = filledForm.data().get(key);
                    if (email != null) {
                        AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                                ConfigFactory.load().getString("aws.accessKey"),
                                ConfigFactory.load().getString("aws.secretKey")));
                        ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(email)).withMessage(new Message().withSubject(new Content().withData("You are invited to the JournWe " + adv.getName())).withBody(new Body().withText(new Content().withData("Hey, Your friend " + usr.getName() + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + shortURL + " to participate in that great adventure. ")))).withSource("adventure@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
                        //The adventure's email address is " + advShortname.getShortname() + "@adventure.journwe.com.
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (key.startsWith("facebook[")) {
                if (us != null) {
                    String fbUser = filledForm.data().get(key);
                    new JournweFacebookChatClient().sendMessage(us.getAccessToken(), "You are invited to the JournWe " + adv.getName() + ". Your friend " + usr.getName() + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + shortURL + " to participate in that great adventure. ", fbUser);
                }
            }
        }

        if (placeI == 1) {
            adv.setFavoritePlaceId(lastPlaceId);
            adv.setPlaceVoteOpen(false);
            new AdventureDAO().save(adv);
        }

        if (timeI == 1) {
            adv.setFavoriteTimeId(lastTimeId);
            adv.setTimeVoteOpen(false);
            new AdventureDAO().save(adv);
        }


        try {
            UserEmail primaryEmail = new UserEmailDAO().getPrimaryEmailOfUser(usr.getId());
            if (primaryEmail != null) {
                AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(primaryEmail.getEmail())).withMessage(new Message().withSubject(new Content().withData("Your new Adventure " + adv.getName())).withBody(new Body().withText(new Content().withData("Hey, We created the adventure " + adv.getName() + " for you! Share it with " + shortURL + ". ")))).withSource("adventure@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
                //The adventure's email address is " + advShortname.getShortname() + "@adventure.journwe.com.
                //advShortname.getShortname() + "@adventure.journwe.com"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ("on".equals(filledForm.get("facebookWall"))) {
            JournweFacebookClient fb = JournweFacebookClient.create(us.getAccessToken());
            Logger.info(filledForm.get("facebookWallPost") + " " + shortURL);
            fb.publishLinkOnMyFeed(filledForm.get("facebookWallPost"), shortURL);

        }

        // Save the creator as default owner of the adventure
        AdventureAuthorization authorization = new AdventureAuthorization();
        authorization.setAdventureId(adv.getId());
        authorization.setUserId(usr.getId());
        authorization.setAuthorizationRole(EAuthorizationRole.ADVENTURE_OWNER);
        new AdventureAuthorizationDAO().save(authorization);

        flash("success", "Congratulations! There goes your adventure. Yeeeehaaaa! The shortURL is " + shortURL);

        return redirect(routes.AdventureController.getIndex(adv.getId()));

    }

    public static Result saveEditable() {
        DynamicForm advForm = form().bindFromRequest();
        String advId = advForm.get("pk");
        if (advId != null && !"".equals(advId)) {
            if (!JournweAuthorization.canEditAdventureTitle(advId))
                return badRequest("You are not authorized to do this.");
            Adventure adv = new AdventureDAO().get(advId);
            String name = advForm.get("name");
            if ("adventureName".equals(name))
                adv.setName(advForm.get("value"));
            else if ("adventureDescription".equals(name)) {
                adv.setDescription(advForm.get("value"));
            }
            new AdventureDAO().save(adv);
        }

        return ok();
    }

    public static Result updateImage(String advId) {
        if (!JournweAuthorization.canEditAdventureImage(advId))
            return badRequest("You are not authorized to do this.");
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
            }

            new AdventureDAO().save(adv);
        } catch (Exception e) {
            return badRequest();
        }

        ObjectNode node = Json.newObject();
        node.put("image", adv.getImage());
        return ok(Json.toJson(node));
    }

    public static Result updatePlaceVoteOpen(String advId) {
        DynamicForm data = form().bindFromRequest();
        Boolean openVote = new Boolean(data.get("voteOpen"));
        Adventure adv = new AdventureDAO().get(advId);
        adv.setPlaceVoteOpen(openVote);
        new AdventureDAO().save(adv);

        new AdventurerNotifier().notifyAdventurers(advId, "The Place Vote is now " + (adv.getPlaceVoteOpen() ? "open" : "closed") + ".", "Place Vote");

        return ok(Json.toJson(adv.getPlaceVoteOpen()));
    }

    public static Result updateTimeVoteOpen(String advId) {
        DynamicForm data = form().bindFromRequest();
        Boolean openVote = new Boolean(data.get("voteOpen"));
        Adventure adv = new AdventureDAO().get(advId);
        adv.setTimeVoteOpen(openVote);
        new AdventureDAO().save(adv);
        return ok(Json.toJson(adv.getTimeVoteOpen()));
    }


    public static Result delete(String advId) {
        for (Adventurer advr : new AdventurerDAO().all(advId))
            new AdventurerDAO().delete(advr);

        for (models.adventure.checklist.Todo todo : new TodoDAO().all(advId))
            new TodoDAO().delete(todo);

        for (CommentThread ct : new CommentThreadDAO<Adventure>().all(advId)) {
            for (Comment c : new CommentDAO().getComments(ct.getThreadId()))
                new CommentDAO().delete(c);
            new CommentThreadDAO<Adventure>().delete(ct);
        }

        // keep shortname to tell visitors it has been deleted
        //new AdventureShortnameDAO().delete(new AdventureShortnameDAO().getShortname(advId));

        //delete s3 objects
        AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                ConfigFactory.load().getString("aws.accessKey"),
                ConfigFactory.load().getString("aws.secretKey")));
        s3.deleteObject(
                S3_BUCKET_ADVENTURE_IMAGES, advId);

        flash("success", "We deleted your adventure " + new AdventureDAO().get(advId).getName());

        new AdventureDAO().delete(advId);

        return redirect(routes.ApplicationController.index());
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
