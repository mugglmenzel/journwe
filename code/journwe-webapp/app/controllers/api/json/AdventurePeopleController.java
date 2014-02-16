package controllers.api.json;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plusDomains.PlusDomains;
import com.google.api.services.plusDomains.model.Person;
import com.restfb.json.JsonObject;
import com.typesafe.config.ConfigFactory;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.io.DefaultIOHandler;
import models.adventure.Adventure;
import models.adventure.AdventureAuthorization;
import models.adventure.AdventureShortname;
import models.adventure.EAuthorizationRole;
import models.adventure.adventurer.Adventurer;
import models.adventure.adventurer.EAdventurerParticipation;
import models.auth.SecuredUser;
import models.authorization.AuthorizationMessage;
import models.authorization.JournweAuthorization;
import models.dao.AdventureAuthorizationDAO;
import models.dao.AdventureShortnameDAO;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.inspiration.InspirationDAO;
import models.dao.manytomany.AdventureToUserDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserSocialDAO;
import models.helpers.JournweFacebookChatClient;
import models.helpers.JournweFacebookClient;
import models.inspiration.Inspiration;
import models.notifications.helper.AdventurerNotifier;
import models.user.EUserRole;
import models.user.User;
import models.user.UserSocial;
import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 28.06.13
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class AdventurePeopleController extends Controller {


    public static Result getAdventurers(final String advId) {
        try {
            return ok(Cache.getOrElse("adventure." + advId + ".adventurers.all", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    for (Adventurer advr : new AdventurerDAO().all(advId))
                        if (advr != null) {
                            ObjectNode node = Json.newObject();
                            node.put("id", advr.getUserId());
                            node.put("link", controllers.html.routes.UserController.getProfile(advr.getUserId()).absoluteURL(request()));

                            User usr = advr.getUserId() != null ? new UserDAO().get(advr.getUserId()) : null;
                            node.put("name", usr != null ? usr.getName().replaceAll(" [^ ]*$", "") : "");
                            node.put("image", usr != null ? usr.getImage() : null);

                            node.put("status", advr.getParticipationStatus().toString());
                            results.add(node);
                        }
                    return Json.toJson(results).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate adventurers for adventure " + advId, e);
            return internalServerError();
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getParticipants(final String advId) {
        if (!new JournweAuthorization(advId).canViewAdventurerParticipants())
            return AuthorizationMessage.notAuthorizedResponse();

        try {
            return ok(Cache.getOrElse("adventure." + advId + ".adventurers.participants", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    for (Adventurer advr : new AdventurerDAO().all(advId))
                        if (advr != null && !EAdventurerParticipation.INVITEE.equals(advr.getParticipationStatus()) && !EAdventurerParticipation.APPLICANT.equals(advr.getParticipationStatus())) {
                            ObjectNode node = Json.newObject();
                            node.put("id", advr.getUserId());
                            node.put("link", controllers.html.routes.UserController.getProfile(advr.getUserId()).absoluteURL(request()));

                            User usr = advr.getUserId() != null ? new UserDAO().get(advr.getUserId()) : null;
                            node.put("name", usr != null ? usr.getName() : "");
                            node.put("image", usr != null ? usr.getImage() : null);

                            node.put("status", advr.getParticipationStatus().toString());
                            results.add(node);
                        }
                    return Json.toJson(results).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate participants for adventure " + advId, e);
            return internalServerError();
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getOtherParticipants(final String advId) {
        if (!new JournweAuthorization(advId).canViewAdventurerParticipants())
            return AuthorizationMessage.notAuthorizedResponse();

        final User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        try {
            return ok(Cache.getOrElse("adventure." + advId + ".adventurers.participants.others", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    for (Adventurer advr : new AdventurerDAO().all(advId))
                        if (advr != null && !usr.getId().equals(advr.getUserId()) && !EAdventurerParticipation.INVITEE.equals(advr.getParticipationStatus()) && !EAdventurerParticipation.APPLICANT.equals(advr.getParticipationStatus())) {
                            ObjectNode node = Json.newObject();
                            node.put("id", advr.getUserId());
                            node.put("link", controllers.html.routes.UserController.getProfile(advr.getUserId()).absoluteURL(request()));

                            User usr = advr.getUserId() != null ? new UserDAO().get(advr.getUserId()) : null;
                            node.put("name", usr != null ? usr.getName() : "");
                            node.put("image", usr != null ? usr.getImage() : null);

                            node.put("status", advr.getParticipationStatus().toString());
                            results.add(node);
                        }
                    return Json.toJson(results).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate participants for adventure " + advId, e);
            return internalServerError();
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getInvitees(final String advId) {
        if (!new JournweAuthorization(advId).canViewAdventurerParticipants())
            return AuthorizationMessage.notAuthorizedResponse();

        try {
            return ok(Cache.getOrElse("adventure." + advId + ".adventurers.invitees", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    for (Adventurer advr : new AdventurerDAO().all(advId))
                        if (advr != null && EAdventurerParticipation.INVITEE.equals(advr.getParticipationStatus())) {
                            ObjectNode node = Json.newObject();
                            node.put("id", advr.getUserId());
                            node.put("link", controllers.html.routes.UserController.getProfile(advr.getUserId()).absoluteURL(request()));

                            User usr = advr.getUserId() != null ? new UserDAO().get(advr.getUserId()) : null;
                            node.put("name", usr != null ? usr.getName() : "");
                            node.put("image", usr != null ? usr.getImage() : null);

                            node.put("status", advr.getParticipationStatus().toString());
                            results.add(node);
                        }
                    return Json.toJson(results).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate invitees for adventure " + advId, e);
            return internalServerError();
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getApplicants(final String advId) {
        if (!new JournweAuthorization(advId).canViewAdventurerParticipants())
            return AuthorizationMessage.notAuthorizedResponse();

        try {
            return ok(Cache.getOrElse("adventure." + advId + ".adventurers.applicants", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<ObjectNode> results = new ArrayList<ObjectNode>();
                    for (Adventurer advr : new AdventurerDAO().all(advId))
                        if (advr != null && EAdventurerParticipation.APPLICANT.equals(advr.getParticipationStatus())) {
                            ObjectNode node = Json.newObject();
                            node.put("id", advr.getUserId());
                            node.put("link", controllers.html.routes.UserController.getProfile(advr.getUserId()).absoluteURL(request()));

                            User usr = advr.getUserId() != null ? new UserDAO().get(advr.getUserId()) : null;
                            node.put("name", usr != null ? usr.getName() : "Unknown");
                            node.put("image", usr != null ? usr.getImage() : null);

                            node.put("status", advr.getParticipationStatus().toString());
                            results.add(node);
                        }
                    return Json.toJson(results).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate applicants for adventure " + advId, e);
            return internalServerError();
        }
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result participateStatus(final String advId, final String statusStr) {
        if (!new JournweAuthorization(advId).canEditAdventurerParticipationStatus())
            return AuthorizationMessage.notAuthorizedResponse();

        EAdventurerParticipation status = EAdventurerParticipation.valueOf(statusStr);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        if (advr != null) {
            advr.setParticipationStatus(status);
            new AdventurerDAO().save(advr);
            new AdventurerNotifier().notifyAdventurers(advId, usr.getName() + " changed the participation status to " + status.name() + ".", "Participation Status");

            clearCache(advId);
        }

        return ok(Json.toJson(advr));
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result adopt(final String advId, final String userId) {
        if (!new JournweAuthorization(advId).canAcceptAdventurerApplicants())
            return AuthorizationMessage.notAuthorizedResponse();

        Adventurer advr = new AdventurerDAO().get(advId, userId);
        if (advr == null) {
            advr = new Adventurer();
            advr.setUserId(userId);
            advr.setAdventureId(advId);
        }
        advr.setParticipationStatus(EAdventurerParticipation.GOING);
        new AdventurerDAO().save(advr);

        AdventureAuthorization authorization = new AdventureAuthorization();
        authorization.setAdventureId(advId);
        authorization.setUserId(userId);
        authorization.setAuthorizationRole(EAuthorizationRole.ADVENTURE_PARTICIPANT);
        new AdventureAuthorizationDAO().save(authorization);

        // Save Adventure-to-User relationship
        Adventure adv = new AdventureDAO().get(advId);
        User usr = new UserDAO().get(userId);
        new AdventureToUserDAO().createManyToManyRelationship(adv, usr);

        clearCache(advId);
        ApplicationController.clearUserCache(userId);

        ObjectNode result = (ObjectNode) Json.toJson(advr);
        result.put("userName", new UserDAO().get(userId).getName());
        return ok(result);
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result deny(final String advId, final String userId) {
        if (!new JournweAuthorization(advId).canAcceptAdventurerApplicants())
            return AuthorizationMessage.notAuthorizedResponse();

        new AdventurerDAO().delete(advId, userId);

        clearCache(advId);
        ApplicationController.clearUserCache(userId);

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

    @Security.Authenticated(SecuredUser.class)
    public static Result postOnMyFacebookWall(String advId) {
        Adventure adv = new AdventureDAO().get(advId);
        Inspiration ins = adv.getInspirationId() != null ? new InspirationDAO().get(adv.getInspirationId()) : null;
        AdventureShortname shortname = new AdventureShortnameDAO().getShortname(advId);

        DynamicForm f = form().bindFromRequest();

        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        JournweFacebookClient fb = JournweFacebookClient.create(us.getAccessToken());
        fb.publishLinkOnMyFeed(f.get("posttext"), controllers.html.routes.AdventureController.getIndexShortname(shortname.getShortname()).absoluteURL(request()), "JournWe  Adventure: " + adv.getName(), "" + (adv.getDescription() == null ? ins.getDescription() : adv.getDescription()), "" + adv.getImage());

        return ok();
    }

    /*
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
    */

    @Security.Authenticated(SecuredUser.class)
    public static Result invite(String advId) {
        if (!new JournweAuthorization(advId).canInviteAdventurerParticipants())
            return AuthorizationMessage.notAuthorizedResponse();
        Adventure adv = new AdventureDAO().get(advId);
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        String shortURL = adv.getShortURL() != null ? adv.getShortURL() : controllers.html.routes.AdventureController.getIndex(adv.getId()).absoluteURL(request());

        DynamicForm f = form().bindFromRequest();
        try {
            if ("email".equals(f.get("type"))) {
                AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(
                        ConfigFactory.load().getString("aws.accessKey"),
                        ConfigFactory.load().getString("aws.secretKey")));
                ses.sendEmail(new SendEmailRequest().withDestination(new Destination().withToAddresses(f.get("value"))).withMessage(new Message().withSubject(new Content().withData("You are invited to the JournWe " + adv.getName())).withBody(new Body().withText(new Content().withData("Hey, Your friend " + usr.getName() + " created the JournWe " + adv.getName() + " and wants you to join! Visit " + shortURL + " to participate in that great adventure. ")))).withSource("adventure@journwe.com").withReplyToAddresses("no-reply@journwe.com"));

                return ok();
            } else if ("facebook".equals(f.get("type"))) {
                String inviteeId = f.get("value");
                Logger.debug("inviting " + inviteeId);
                UserSocial us = new UserSocialDAO().findByUserIdAndProvider("facebook", usr.getId());
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


                //FIXME: This doesn't work!
                Adventurer advr = new AdventurerDAO().get(adv.getId(), invitee.getId());
                if (advr == null) {
                    advr = new Adventurer();
                    advr.setUserId(invitee.getId());
                    advr.setAdventureId(adv.getId());
                    advr.setParticipationStatus(EAdventurerParticipation.INVITEE);
                    new AdventurerDAO().save(advr);
                }

                AdventureAuthorization authorization = new AdventureAuthorization();
                authorization.setAdventureId(adv.getId());
                authorization.setUserId(invitee.getId());
                authorization.setAuthorizationRole(EAuthorizationRole.ADVENTURE_PARTICIPANT);
                new AdventureAuthorizationDAO().save(authorization);

                clearCache(advId);
                ApplicationController.clearUserCache(invitee.getId());

                return ok();
            }
        } catch (Exception e) {
            Logger.error("Couldn't invite adventurer.", e);
        }

        return badRequest();
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result autocompleteFacebook() {
        DynamicForm form = form().bindFromRequest();
        String input = form.get("input");
        List<ObjectNode> results = new ArrayList<ObjectNode>();

        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("facebook", usr.getId());
        JournweFacebookClient fb = JournweFacebookClient.create(us.getAccessToken());
        List<JsonObject> friends = fb.getMyFriendsAsJson();

        for (JsonObject friend : friends)
            if (friend.getString("name").toLowerCase().contains(input.toLowerCase())) {
                ObjectNode node = Json.newObject();
                node.put("id", friend.getString("id"));
                node.put("name", friend.getString("name"));
                results.add(node);
            }

        return ok(Json.toJson(results));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result autocompleteFoursquare() {
        DynamicForm form = form().bindFromRequest();
        String input = form.get("input");
        List<ObjectNode> results = new ArrayList<ObjectNode>();

        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("foursquare", usr.getId());

        try {
            Logger.debug("connecting to foursquare with " + ConfigFactory.load().getString("play-authenticate.foursquare.clientId") + " -> " + ConfigFactory.load().getString("play-authenticate.foursquare.clientSecret") + " -> " + OAuth2AuthProvider.Registry.get("foursquare").getUrl());
            FoursquareApi four = new FoursquareApi(ConfigFactory.load().getString("play-authenticate.foursquare.clientId"), ConfigFactory.load().getString("play-authenticate.foursquare.clientSecret"), OAuth2AuthProvider.Registry.get("foursquare").getUrl(), us.getAccessToken(), new DefaultIOHandler());
            fi.foyt.foursquare.api.Result<fi.foyt.foursquare.api.entities.UserGroup> friends = four.usersFriends(us.getSocialId());
            Logger.debug("foursquare api request returned " + friends);

            for (fi.foyt.foursquare.api.entities.CompactUser friend : friends.getResult().getItems())
                if (new String(friend.getFirstName() + friend.getLastName()).toLowerCase().contains(input.toLowerCase())) {
                    ObjectNode node = Json.newObject();
                    node.put("id", friend.getId());
                    node.put("name", friend.getFirstName() + " " + friend.getLastName());
                    results.add(node);
                }
        } catch (FoursquareApiException e) {
            Logger.error("Could not fetch friends from Foursquare!");
        }

        return ok(Json.toJson(results));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result autocompleteGoogle() {
        DynamicForm form = form().bindFromRequest();
        String input = form.get("input");
        List<ObjectNode> results = new ArrayList<ObjectNode>();

        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        UserSocial us = new UserSocialDAO().findBySocialId("google", usr.getId());

        try {
            GoogleCredential credential = new GoogleCredential.Builder().setClientSecrets(ConfigFactory.load().getString("play-authenticate.google.clientId"), ConfigFactory.load().getString("play-authenticate.google.clientSecret")).setTransport(new NetHttpTransport()).setJsonFactory(new JacksonFactory()).build().setFromTokenResponse(new TokenResponse().setAccessToken(us.getAccessToken()));

            for (Person friend : new PlusDomains(new NetHttpTransport(), new JacksonFactory(), credential).people().list(us.getSocialId(), "circled").execute().getItems())
                if (new String(friend.getDisplayName() + " " + friend.getName() + " " + friend.getNickname()).contains(input.toLowerCase())) {
                    ObjectNode node = Json.newObject();
                    node.put("id", friend.getId());
                    node.put("name", friend.getDisplayName() + " (" + friend.getName() + ")");
                    results.add(node);
                }
        } catch (Exception e) {
            Logger.error("Could not fetch friends from Google!", e);
        }

        return ok(Json.toJson(results));
    }


    private static void clearCache(final String advId) {
        Cache.remove("adventure." + advId + ".adventurers.all");
        Cache.remove("adventure." + advId + ".adventurers.participants.others");
        Cache.remove("adventure." + advId + ".adventurers.participants");
        Cache.remove("adventure." + advId + ".adventurers.invitees");
        Cache.remove("adventure." + advId + ".adventurers.applicants");
    }

}
