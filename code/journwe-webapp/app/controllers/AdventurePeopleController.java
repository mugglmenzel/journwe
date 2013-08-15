package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.restfb.json.JsonObject;
import com.typesafe.config.ConfigFactory;
import models.Inspiration;
import models.adventure.*;
import models.auth.SecuredBetaUser;
import models.authorization.AuthorizationMessage;
import models.authorization.JournweAuthorization;
import models.dao.*;
import models.helpers.JournweFacebookChatClient;
import models.helpers.JournweFacebookClient;
import models.user.EUserRole;
import models.user.User;
import models.user.UserSocial;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.OAuthUserServicePlugin;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 28.06.13
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class AdventurePeopleController extends Controller {


    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getAdventurers(String advId) {
        if (!JournweAuthorization.canViewAdventurerParticipants(advId))
            return AuthorizationMessage.notAuthorizedResponse();
        return ok(Json.toJson(new AdventurerDAO().all(advId)));
    }


    public static Result participate(String advId) {

        //BETA activation
        if (!PlayAuthenticate.isLoggedIn(Http.Context.current().session())) {
            PlayAuthenticate.storeOriginalUrl(Http.Context.current());
            response().setCookie(OAuthUserServicePlugin.USER_ROLE_ON_REGISTER, EUserRole.BETA.toString());
            return redirect(PlayAuthenticate.getProvider("facebook").getUrl());
        } else {
            User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

            Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
            if (advr == null) {
                advr = new Adventurer();
                advr.setUserId(usr.getId());
                advr.setAdventureId(advId);
                advr.setParticipationStatus(EAdventurerParticipation.APPLICANT);
                new AdventurerDAO().save(advr);
            }

            return AdventureController.getIndex(advId);
        }
    }


    public static Result participateStatus(String advId, String statusStr) {
        if (!JournweAuthorization.canEditAdventurerParticipationStatus(advId))
            return AuthorizationMessage.notAuthorizedResponse();

        EAdventurerParticipation status = EAdventurerParticipation.valueOf(statusStr);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        if (advr != null) {
            advr.setParticipationStatus(status);
            new AdventurerDAO().save(advr);
        }

        return ok(Json.toJson(advr));
    }

    public static Result leave(String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());

        new AdventurerDAO().delete(advr);

        flash("success", "You left the adventure " + new AdventureDAO().get(advId).getName());

        if (new AdventurerDAO().count(advId) > 0)
            return redirect(routes.ApplicationController.index());
        else
            return AdventureController.delete(advId);
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result adopt(String advId, String userId) {
        if (!JournweAuthorization.canAcceptAdventurerApplicants(advId))
            return AuthorizationMessage.notAuthorizedResponse();
        User usr = new UserDAO().get(userId);
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        if (advr == null) {
            advr = new Adventurer();
            advr.setUserId(usr.getId());
            advr.setAdventureId(advId);
        }
        advr.setParticipationStatus(EAdventurerParticipation.GOING);
        new AdventurerDAO().save(advr);

        AdventureAuthorization authorization = new AdventureAuthorization();
        authorization.setAdventureId(advId);
        authorization.setUserId(usr.getId());
        authorization.setAuthorizationRole(EAuthorizationRole.ADVENTURE_PARTICIPANT);
        new AdventureAuthorizationDAO().save(authorization);


        ObjectNode result = (ObjectNode) Json.toJson(advr);
        result.put("userName", usr.getName());
        return ok(result);
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


    public static Result postOnMyFacebookWall(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        Inspiration ins = Inspiration.fromId(adv.getInspirationId());
        ins = ins != null ? new InspirationDAO().get(ins.getCategoryId(), ins.getInspirationId()) : ins;
        AdventureShortname shortname = new AdventureShortnameDAO().getShortname(advId);

        DynamicForm f = form().bindFromRequest();

        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        JournweFacebookClient fb = JournweFacebookClient.create(us.getAccessToken());
        fb.publishLinkOnMyFeed(f.get("posttext"), routes.AdventureController.getIndexShortname(shortname.getShortname()).absoluteURL(request()), "JournWe  Adventure: " + adv.getName(), "" + (adv.getDescription() == null ? ins.getDescription() : adv.getDescription()), "" + adv.getImage());

        return ok();
    }


    public static Result inviteViaEmail(String advId) {
        if (!JournweAuthorization.canInviteAdventurerParticipants(advId))
            return AuthorizationMessage.notAuthorizedResponse();
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


    public static Result invite(String advId) {
        if (!JournweAuthorization.canInviteAdventurerParticipants(advId))
            return AuthorizationMessage.notAuthorizedResponse();
        Adventure adv = new AdventureDAO().get(advId);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        String shortURL = routes.AdventureController.getIndex(adv.getId()).absoluteURL(request());

        DynamicForm f = form().bindFromRequest();
        try {
            if ("email".equals(f.get("type"))) {
                AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(f.get("value"))).withMessage(new Message().withSubject(new Content().withData("You are invited to the JournWe " + adv.getName())).withBody(new Body().withText(new Content().withData("Hey, Your friend " + usr.getName() + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + shortURL + " to participate in that great adventure. ")))).withSource("adventure@journwe.com").withReplyToAddresses("no-reply@journwe.com"));

                return ok();
            } else if ("facebook".equals(f.get("type"))) {
                UserSocial us = new UserSocialDAO().findByUserId("facebook", usr.getId());
                new JournweFacebookChatClient().sendMessage(us.getAccessToken(), "You are invited to the JournWe " + adv.getName() + ". Your friend " + usr.getName() + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + shortURL + " to participate in that great adventure. ", f.get("value"));

                return ok();
            }
        } catch (Exception e) {
            Logger.error("Couldn't invite adventurer.", e);
        }

        return badRequest();
    }

    public static Result autocompleteFacebook() {
        DynamicForm form = form().bindFromRequest();
        String input = form.get("input");
        List<ObjectNode> results = new ArrayList<ObjectNode>();

        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        JournweFacebookClient fb = JournweFacebookClient.create(us.getAccessToken());
        List<JsonObject> friends = fb.getMyFriendsAsJson();

        for (JsonObject friend : friends)
            if (friend.getString("name").contains(input)) {
                ObjectNode node = Json.newObject();
                node.put("id", friend.getString("id"));
                node.put("name", friend.getString("name"));
                results.add(node);
            }

        return ok(Json.toJson(results));
    }

}
