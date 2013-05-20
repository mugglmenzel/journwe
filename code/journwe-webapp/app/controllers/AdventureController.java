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
import controllers.auth.SecuredAdminUser;
import models.*;
import models.dao.*;
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
import java.util.Map;

import static com.rosaloves.bitlyj.Bitly.shorten;
import static play.data.Form.form;

public class AdventureController extends Controller {

    private static final String S3_BUCKET_ADVENTURE_IMAGES = "journwe-adventure-images";

    private static DynamicForm advForm = form();

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getIndex(String id) {
        Adventure adv = new AdventureDAO().get(id);
        User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(id, usr.getId());
        if (advr == null)
            return ok(getPublic.render(adv, new InspirationDAO().get(adv.getInspirationId())));
        else
            return ok(getIndex.render(adv, new InspirationDAO().get(adv.getInspirationId())));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getIndexShortname(String shortname) {
        return getIndex(new AdventureShortnameDAO().get(shortname).getAdventureId());
    }


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getAdventurers(String id) {
        User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventure adv = new AdventureDAO().get(id);
        Adventurer advr = new AdventurerDAO().get(id, usr.getId());

        return ok(getAdventurers.render(adv, new InspirationDAO().get(adv.getInspirationId()), new AdventurerDAO().all(id), advr == null ? null : advr.getParticipationStatus()));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result getTodos(String id) {
        User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventure adv = new AdventureDAO().get(id);

        return ok(getTodos.render(adv, new InspirationDAO().get(adv.getInspirationId()), new TodoDAO().all(id), usr.getId()));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result addTodo(String id) {

        DynamicForm requestData = form().bindFromRequest();

        User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        models.Todo todo = new models.Todo();
        todo.setAdventureId(id);
        todo.setUserId(usr.getId());
        todo.setTitle(requestData.get("title"));

        new TodoDAO().save(todo);

        return ok(Json.toJson(todo));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result setTodo(String id, String tid) {

        DynamicForm requestData = form().bindFromRequest();

        models.Todo todo = new TodoDAO().get(tid, id);

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
    public static Result save() {
        Form<Adventure> filledAdvForm = form(Adventure.class).bindFromRequest();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart image = body.getFile("image");

        if (advForm.bindFromRequest().hasErrors()) {
            return badRequest(create.render(advForm.bindFromRequest(),
                    new InspirationDAO().allOptionsMap(50)));

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
                    AdventureShortname shortname = new AdventureShortname(filledAdvForm.data().get("shortname"), adv.getId());
                    new AdventureShortnameDAO().save(shortname);

                    String shortURL = routes.AdventureController.getIndexShortname(shortname.getShortname()).absoluteURL(request());
                    try {
                        shortURL = request().host().contains("localhost") ?
                                routes.AdventureController.getIndexShortname(shortname.getShortname()).absoluteURL(request()) :
                                Jmp.as(ConfigFactory.load().getString("bitly.username"), ConfigFactory.load().getString("bitly.apiKey")).call(shorten(routes.AdventureController.getIndexShortname(shortname.getShortname()).absoluteURL(request()))).getShortUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flash("success",
                            "Saved Adventure with image " + adv.getImage()
                                    + ".");

                    User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

                    Adventurer advr = new Adventurer();
                    advr.setAdventureId(adv.getId());
                    advr.setUserId(usr.getId());
                    advr.setParticipationStatus(EAdventurerParticipation.GOING);
                    new AdventurerDAO().save(advr);

                    try {
                        AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                                ConfigFactory.load().getString("aws.accessKey"),
                                ConfigFactory.load().getString("aws.secretKey")));
                        String primaryEmail = new UserEmailDAO().getPrimaryEmailOfUser(usr.getId()).getEmail();
                        Logger.info("got primary email: " + primaryEmail);
                        ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(primaryEmail)).withMessage(new Message().withSubject(new Content().withData("Your new Adventure " + adv.getName())).withBody(new Body().withText(new Content().withData("Hey, We created the adventure " + adv.getName() + " for you! Share it with " + shortURL + ". The adventure's email address is " + shortname.getShortname() + "@journwe.com.")))).withSource(shortname.getShortname() + "@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return ok(created.render(new AdventureDAO().get(adv.getId()), ins, shortURL, shortname.getShortname()));

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
                return internalServerError(create.render(advForm.bindFromRequest(),
                        new InspirationDAO()
                                .allOptionsMap(50)));
            }
        }

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

    public static Result checkShortname() {
        DynamicForm requestData = form().bindFromRequest();

        ObjectNode node = Json.newObject();
        node.put("value", requestData.get("value"));
        node.put("valid", new AdventureShortnameDAO().exists(requestData.get("value")) ? 0 : 1);
        node.put("message", "Shortname already exists!");
        Logger.info(node.toString());
        return ok(Json.toJson(node));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result participate(String advId) {
        User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
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
        User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        if (advr != null) {
            advr.setParticipationStatus(status);
            new AdventurerDAO().save(advr);
        }
        return redirect(routes.AdventureController.getAdventurers(advId));

    }

}
