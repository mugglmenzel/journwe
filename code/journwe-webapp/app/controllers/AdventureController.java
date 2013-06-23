package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.rosaloves.bitlyj.Jmp;
import com.typesafe.config.ConfigFactory;
import controllers.auth.SecuredAdminUser;
import models.Inspiration;
import models.adventure.*;
import models.adventure.checklist.EStatus;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.dao.*;
import models.helpers.JournweFacebookClient;
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
import views.html.adventure.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;

import static com.rosaloves.bitlyj.Bitly.shorten;
import static play.data.Form.form;

public class AdventureController extends Controller {

    private static final String S3_BUCKET_ADVENTURE_IMAGES = "journwe-adventure-images";

    private static DynamicForm advForm = form();
    private static Form<TimeOption> timeForm = form(TimeOption.class);

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getIndex(String id) {
        Adventure adv = new AdventureDAO().get(id);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(id, usr.getId());
        if (advr == null)
            return ok(getPublic.render(adv, new InspirationDAO().get(adv.getInspirationId())));
        else
            return ok(getIndex.render(adv, new InspirationDAO().get(adv.getInspirationId()), advr, timeForm));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getIndexShortname(String shortname) {
        return getIndex(new AdventureShortnameDAO().get(shortname).getAdventureId());
    }


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getAdventurers(String id) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventure adv = new AdventureDAO().get(id);
        Adventurer advr = new AdventurerDAO().get(id, usr.getId());

        return ok(getAdventurers.render(adv, new InspirationDAO().get(adv.getInspirationId()), advr, timeForm));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getTodos(String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventure adv = new AdventureDAO().get(advId);
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());

        return ok(getTodos.render(adv, new InspirationDAO().get(adv.getInspirationId()), advr, timeForm));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result addTodo(String id) {

        DynamicForm requestData = form().bindFromRequest();

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        models.adventure.checklist.Todo todo = new models.adventure.checklist.Todo();
        todo.setAdventureId(id);
        todo.setUserId(usr.getId());
        todo.setTitle(requestData.get("title"));

        new TodoDAO().save(todo);

        return ok(Json.toJson(todo));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result setTodo(String id, String tid) {

        DynamicForm requestData = form().bindFromRequest();

        models.adventure.checklist.Todo todo = new TodoDAO().get(tid, id);

        String status = requestData.get("status").toUpperCase();
        todo.setStatus(EStatus.valueOf(status));

        new TodoDAO().save(todo);

        return ok(Json.toJson(todo)); //TODO: Error handling
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result deleteTodo(String id, String tid) {

        new TodoDAO().delete(tid, id);

        return ok(); //TODO: Error handling
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result create() {
        return createFromInspiration(null);
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result createFromInspiration(String insId) {
        Map<String, String> inspireOptions = new InspirationDAO()
                .allOptionsMap(50);
        Form<Adventure> advFilledForm = form(Adventure.class);
        if (insId != null && !"".equals(insId)) {
            Adventure adv = new Adventure();
            adv.setInspirationId(insId);
            advFilledForm = advFilledForm.fill(adv);
        } else advFilledForm = advFilledForm.fill(new Adventure());
        return ok(create.render(form().fill(advFilledForm.data()), inspireOptions));

    }

    @Security.Authenticated(SecuredAdminUser.class)
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
                Inspiration ins = new InspirationDAO().get(adv.getInspirationId());

                Logger.info("got image file " + image.getFilename());

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
                                .allOptionsMap(50)));
            }
        }
        return badRequest(create.render(filledForm,
                new InspirationDAO().allOptionsMap(50)));


    }


    public static Result save() {
        DynamicForm filledForm = advForm.bindFromRequest();


        Adventure adv = new Adventure();
        adv.setName(filledForm.get("name"));
        new AdventureDAO().save(adv);

        //List<PlaceOption> placeOptions = new ArrayList<PlaceOption>();
        //List<TimeOption> timeOptions = new ArrayList<TimeOption>();
        int placeI = 1;
        int timeI = 1;
        for (String key : filledForm.data().keySet()) {
            if (key.startsWith("place[")) {
                PlaceOption po = new PlaceOption();
                po.setAdventureId(adv.getId());
                po.setName("Option " + placeI);
                placeI++;
                po.setGoogleMapsAddress(filledForm.data().get(key));
                new PlaceOptionDAO().save(po);
                //placeOptions.add(po);
            }
            if (key.startsWith("time[")) {
                try {
                    TimeOption to = new TimeOption();
                    to.setAdventureId(adv.getId());
                    to.setName("Option " + timeI);
                    timeI++;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println("parsing date from " + filledForm.data().get(key));
                    System.out.println("start date: " + filledForm.data().get(key).split(",")[0]);
                    System.out.println("end date: " + filledForm.data().get(key).split(",")[1]);
                    System.out.println("parsed start: " + sdf.parse(filledForm.data().get(key).split(",")[0]));
                    System.out.println("parsed end: " + sdf.parse(filledForm.data().get(key).split(",")[1]));

                    to.setStartDate(sdf.parse(filledForm.data().get(key).split(",")[0]));
                    to.setEndDate(sdf.parse(filledForm.data().get(key).split(",")[1]));

                    new TimeOptionDAO().save(to);
                    //timeOptions.add(to);

                } catch (Exception e) {
                    timeI--;
                }
            }
        }


        String shortname = filledForm.get("shortname");
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

        flash("success", "Congratulations! There goes your adventure. Yeeeehaaaa!<br>The shortURL is " + shortURL);

        return getIndex(adv.getId());

    }

    public static Result saveEditable() {
        DynamicForm advForm = form().bindFromRequest();
        String advId = advForm.get("pk");
        if (advId != null && !"".equals(advId)) {
            Adventure adv = new AdventureDAO().get(advId);
            String name = advForm.get("name");
            if ("adventureName".equals(name))
                adv.setName(advForm.get("value"));
            else if ("adventureDescription".equals(name))
                adv.setDescription(advForm.get("value"));
            new AdventureDAO().save(adv);
        }

        return ok();
    }


    public static Result leave(String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());

        new AdventurerDAO().delete(advr);

        flash("success", "You left the adventure " + new AdventureDAO().get(advId).getName());

        return redirect(routes.ApplicationController.index());
    }


    public static Result delete(String advId) {
        for (Adventurer advr : new AdventurerDAO().all(advId))
            new AdventurerDAO().delete(advr);

        for (models.adventure.checklist.Todo todo : new TodoDAO().all(advId))
            new TodoDAO().delete(todo);

        for (CommentThread ct : new CommentThreadDAO<Adventure>().getCommentThreads(advId)) {
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

    public static Result postOnMyFacebookWall(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        Inspiration ins = new InspirationDAO().get(adv.getInspirationId());
        AdventureShortname shortname = new AdventureShortnameDAO().getShortname(advId);

        DynamicForm f = form().bindFromRequest();

        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        JournweFacebookClient fb = JournweFacebookClient.create(us.getAccessToken());
        fb.publishLinkOnMyFeed(f.get("posttext"), routes.AdventureController.getIndexShortname(shortname.getShortname()).absoluteURL(request()), "JournWe  Adventure: " + adv.getName(), "" + (adv.getDescription() == null ? ins.getDescription() : adv.getDescription()), "" + adv.getImage());

        return ok();
    }

    /*
    public static Result postOnMyTwitterStream(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        Inspiration ins = new InspirationDAO().get(adv.getInspirationId());
        AdventureShortname shortname = new AdventureShortnameDAO().getShortname(advId);

        DynamicForm f = form().bindFromRequest();

        ObjectNode node = Json.newObject();
        node.put("url", routes.AdventureController.getIndexShortname(shortname.getShortname()).absoluteURL(request()));

        return ok(Json.toJson(node));
    }
    */

    public static Result inviteViaEmail(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        AdventureShortname shortname = new AdventureShortnameDAO().getShortname(advId);

        DynamicForm f = form().bindFromRequest();
        for (String email : f.get("email").split(",")) {
            try {
                AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(email)).withMessage(new Message().withSubject(new Content().withData("Invitation to join my Adventure " + adv.getName() + " on JournWe.com")).withBody(new Body().withText(new Content().withData(f.get("emailtext"))))).withSource(shortname.getShortname() + "@adventure.journwe.com").withReplyToAddresses(shortname.getShortname() + "@adventure.journwe.com"));

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return ok();
    }


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result participate(String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        if (advr == null) {
            advr = new Adventurer();
            advr.setUserId(usr.getId());
            advr.setAdventureId(advId);
            advr.setParticipationStatus(EAdventurerParticipation.APPLICANT);
            new AdventurerDAO().save(advr);
        }

        return redirect(routes.AdventureController.getAdventurers(advId));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result adopt(String advId, String userId) {
        User usr = new UserDAO().get(userId);
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        if (advr == null) {
            advr = new Adventurer();
            advr.setUserId(usr.getId());
            advr.setAdventureId(advId);
        }
        advr.setParticipationStatus(EAdventurerParticipation.GOING);
        new AdventurerDAO().save(advr);


        return redirect(routes.AdventureController.getAdventurers(advId));
    }


    public static Result participateStatus(String advId, String statusStr) {
        EAdventurerParticipation status = EAdventurerParticipation.valueOf(statusStr);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        if (advr != null) {
            advr.setParticipationStatus(status);
            new AdventurerDAO().save(advr);
        }
        return redirect(routes.AdventureController.getAdventurers(advId));

    }

    public static Result getTime(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        Inspiration ins = new InspirationDAO().get(adv.getInspirationId());
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        return ok(getTime.render(adv, ins, advr, timeForm));
    }

    public static Result saveTime(String advId) {
        TimeOption opt = form(TimeOption.class).bindFromRequest().get();
        opt.setAdventureId(advId);
        new TimeOptionDAO().save(opt);
        return getTime(advId);
    }

}
