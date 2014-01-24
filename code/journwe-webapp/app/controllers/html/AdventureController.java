package controllers.html;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.feth.play.module.pa.PlayAuthenticate;
import com.rosaloves.bitlyj.Jmp;
import com.typesafe.config.ConfigFactory;
import controllers.api.json.AdventureFileController;
import controllers.api.json.AdventureTimeController;
import controllers.routes;
import models.adventure.*;
import models.adventure.adventurer.Adventurer;
import models.adventure.adventurer.EAdventurerParticipation;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.auth.SecuredAdminUser;
import models.auth.SecuredUser;
import models.authorization.JournweAuthorization;
import models.dao.*;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.adventure.PlaceOptionDAO;
import models.dao.adventure.TimeOptionDAO;
import models.dao.inspiration.InspirationDAO;
import models.dao.manytomany.AdventureToUserDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserEmailDAO;
import models.dao.user.UserSocialDAO;
import models.helpers.JournweFacebookChatClient;
import models.helpers.JournweFacebookClient;
import models.inspiration.Inspiration;
import models.user.EUserRole;
import models.user.User;
import models.user.UserEmail;
import models.user.UserSocial;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.create;
import views.html.adventure.getIndex;
import views.html.adventure.getPublic;

import java.text.SimpleDateFormat;
import java.util.Map;

import static com.rosaloves.bitlyj.Bitly.shorten;
import static play.data.Form.form;

public class AdventureController extends Controller {

    public static final String S3_BUCKET_ADVENTURE_IMAGES = "journwe-adventure-images";

    private static final AWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey"));

    private static DynamicForm advForm = form();


    public static Result getIndex(String id) {
        Adventure adv = new AdventureDAO().get(id);
        if (adv == null) return badRequest();
        else {
            User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
            Adventurer advr = usr != null ? new AdventurerDAO().get(id, usr.getId()) : null;
            Inspiration ins = adv.getInspirationId() != null ? new InspirationDAO().get(adv.getInspirationId()) : null;
            if (advr == null || EAdventurerParticipation.APPLICANT.equals(advr.getParticipationStatus()) || EAdventurerParticipation.INVITEE.equals(advr.getParticipationStatus()) || !SecuredUser.isAuthorized(PlayAuthenticate.getUser(Http.Context.current())))
                return ok(getPublic.render(adv, ins));
            else
                return ok(getIndex.render(adv, ins, advr));
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getIndexShortname(String shortname) {
        return getIndex(new AdventureShortnameDAO().get(shortname).getAdventureId());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result create() {
        return createFromInspiration(null);
    }

    //TODO: adapt to composite inspiration id
    @Security.Authenticated(SecuredUser.class)
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

    @Security.Authenticated(SecuredUser.class)
    public static Result save() {
        DynamicForm filledForm = advForm.bindFromRequest();

        Adventure adv = new Adventure();
        adv.setName(filledForm.get("name"));
        new AdventureDAO().save(adv);

        if (filledForm.get("inspirationId") != null) {
            Inspiration ins = new InspirationDAO().get(filledForm.get("inspirationId"));
            if (ins != null) {
                adv.setInspirationId(ins.getId());
                adv.setDescription(ins.getDescription());
                // Save Adventure-to-Inspiration relationship TODO ?
                // new AdventureToInspirationDAO().createManyToManyRelationship(adv,usr);
            }
        }

        String shortURL = controllers.html.routes.AdventureController.getIndex(adv.getId()).absoluteURL(request());

        String shortname = filledForm.get("shortname");
        if (shortname != null) {
            AdventureShortname advShortname = new AdventureShortname(shortname, adv.getId());
            new AdventureShortnameDAO().save(advShortname);
            shortURL = controllers.html.routes.AdventureController.getIndexShortname(advShortname.getShortname()).absoluteURL(request());
        }


        try {
            shortURL = request().host().contains("localhost") ?
                    shortURL :
                    Jmp.as(ConfigFactory.load().getString("bitly.username"), ConfigFactory.load().getString("bitly.apiKey")).call(shorten(shortURL)).getShortUrl();
            adv.setShortURL(shortURL);

            new AdventureDAO().save(adv);
        } catch (Exception e) {
            e.printStackTrace();
        }


        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        UserSocial us = new UserSocialDAO().findByUserId(usr.getId());

        // ADVENTURER
        Adventurer advr = new Adventurer();
        advr.setAdventureId(adv.getId());
        advr.setUserIdRangeKey(usr.getId());
        advr.setUserId(usr.getId());
        advr.setParticipationStatus(EAdventurerParticipation.GOING);
        new AdventurerDAO().save(advr);

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
                po.setAddress(filledForm.data().get(key).split("/")[0]);
                po.setLatitude(new Double(filledForm.data().get(key).split("/")[1]));
                po.setLongitude(new Double(filledForm.data().get(key).split("/")[2]));
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
                    String inviteeId = filledForm.data().get(key);
                    new JournweFacebookChatClient().sendMessage(us.getAccessToken(), "You are invited to the JournWe " + adv.getName() + ". Your friend " + usr.getName() + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + shortURL + " to participate in that great adventure. ", inviteeId);

                    UserSocial inviteeSoc = new UserSocialDAO().findBySocialId("facebook", inviteeId);

                    User invitee = inviteeSoc != null && inviteeSoc.getUserId() != null ? new UserDAO().get(inviteeSoc.getUserId()) : null;
                    Logger.debug("got invitee " + invitee);
                    if (invitee == null) {
                        invitee = new User();
                        invitee.setName(JournweFacebookClient.create(us.getAccessToken()).getFacebookUser(inviteeId).getName());
                        invitee.setRole(EUserRole.INVITEE);
                        new UserDAO().save(invitee);
                        Logger.debug("created invitee as user");
                    }

                    if (inviteeSoc == null) {
                        inviteeSoc = new UserSocial();
                        inviteeSoc.setProvider("facebook");
                        inviteeSoc.setSocialId(inviteeId);
                    }
                    inviteeSoc.setUserId(invitee.getId());
                    new UserSocialDAO().save(inviteeSoc);


                    Adventurer inviteeAdvr = new AdventurerDAO().get(adv.getId(), invitee.getId());
                    if (inviteeAdvr == null) {
                        inviteeAdvr = new Adventurer();
                        inviteeAdvr.setUserId(invitee.getId());
                        inviteeAdvr.setAdventureId(adv.getId());
                        inviteeAdvr.setParticipationStatus(EAdventurerParticipation.INVITEE);
                        new AdventurerDAO().save(inviteeAdvr);
                    }

                    AdventureAuthorization authorization = new AdventureAuthorization();
                    authorization.setAdventureId(adv.getId());
                    authorization.setUserId(invitee.getId());
                    authorization.setAuthorizationRole(EAuthorizationRole.ADVENTURE_PARTICIPANT);
                    new AdventureAuthorizationDAO().save(authorization);
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


        // CREATE ADVENTURE EMAIL ADDRESS
        AmazonSQS sqs = new AmazonSQSClient(credentials);
        sqs.sendMessage(new SendMessageRequest().withQueueUrl("journwe-email-bond").withMessageBody(adv.getId()));
        AmazonSNS sns = new AmazonSNSClient(credentials);
        sns.publish(new PublishRequest().withTopicArn("arn:aws:sns:us-east-1:561785394163:journwe-email-bond").withSubject("Wake Up, Bond!").withMessage("New Email Addresses!"));


        try {
            UserEmail primaryEmail = new UserEmailDAO().getPrimaryEmailOfUser(usr.getId());
            if (primaryEmail != null) {
                AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(credentials);
                ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(primaryEmail.getEmail(), adv.getId() + "@journwe.com")).withMessage(new Message().withSubject(new Content().withData("Your new Adventure " + adv.getName())).withBody(new Body().withText(new Content().withData("Hey, We created the adventure " + adv.getName() + " for you! Share it with " + shortURL + ". ")))).withSource("adventure@journwe.com").withReplyToAddresses("no-reply@journwe.com"));
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

        // Save Adventure-to-User relationship
        new AdventureToUserDAO().createManyToManyRelationship(adv,usr);

        // Save the creator as default owner of the adventure
        AdventureAuthorization authorization = new AdventureAuthorization();
        authorization.setAdventureId(adv.getId());
        authorization.setUserId(usr.getId());
        authorization.setAuthorizationRole(EAuthorizationRole.ADVENTURE_OWNER);
        new AdventureAuthorizationDAO().save(authorization);

        ApplicationController.clearUserCache(usr.getId());

        flash("success", "Congratulations! There goes your adventure. Yeeeehaaaa! The shortURL is " + shortURL);

        return redirect(controllers.html.routes.AdventureController.getIndex(adv.getId()));

    }

    @Security.Authenticated(SecuredUser.class)
    public static Result saveEditable() {
        DynamicForm advForm = form().bindFromRequest();
        String advId = advForm.get("pk");
        if (advId != null && !"".equals(advId)) {
            if (!new JournweAuthorization(advId).canEditAdventureTitle())
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


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result delete(String advId) {
        String name = new AdventureDAO().get(advId).getName();
        new AdventureDAO().deleteFull(advId);
        //delete s3 objects
        AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                ConfigFactory.load().getString("aws.accessKey"),
                ConfigFactory.load().getString("aws.secretKey")));
        for (S3ObjectSummary obj : s3.listObjects(S3_BUCKET_ADVENTURE_IMAGES, advId).getObjectSummaries())
            s3.deleteObject(
                    S3_BUCKET_ADVENTURE_IMAGES, obj.getKey());
        for (S3ObjectSummary obj : s3.listObjects(AdventureFileController.S3_BUCKET, advId).getObjectSummaries())
            s3.deleteObject(
                    AdventureFileController.S3_BUCKET, obj.getKey());


        flash("success", "We deleted your adventure " + name);

        return redirect(controllers.html.routes.ApplicationController.index());
    }



}