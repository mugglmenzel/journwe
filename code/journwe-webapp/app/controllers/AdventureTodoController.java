package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import com.journwe.productadvertising.webservice.client.*;
import com.journwe.productadvertising.webservice.client.enums.EMarketplaceDomain;
import com.typesafe.config.ConfigFactory;
import models.Inspiration;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.checklist.EStatus;
import models.auth.SecuredBetaUser;
import models.dao.*;
import models.helpers.AWSProductAdvertisingAPIHelper;
import models.user.User;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.getTodos;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.*;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.06.13
 * Time: 12:16
 * To change this template use File | Settings | File Templates.
 */
public class AdventureTodoController extends Controller {

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getTodos(String advId, String userId) {
        List<ObjectNode> results = new ArrayList<ObjectNode>();

        for (models.adventure.checklist.Todo todo : new TodoDAO().all(userId, advId)) {
            ItemSearchRequest shared = new ItemSearchRequest();
            shared.setKeywords(todo.getTitle());
            shared.setSearchIndex("All");
            List<ItemSearchRequest> itemSearches = new ArrayList<ItemSearchRequest>();
            itemSearches.add(shared);


            Holder<OperationRequest> opOut = new Holder<OperationRequest>();
            Holder<List<Items>> itemsOut = new Holder<List<Items>>();

            //EMarketplaceDomain.US.getMarketPlaceDomain()
            AWSProductAdvertisingAPIHelper.getService().getAWSECommerceServicePort().itemSearch(null, ConfigFactory.load().getString("aws.productadvertising.accessKey"), "jourwheradvem-21", "Single", "False", shared, itemSearches, opOut, itemsOut);
            List<String> productURLs = new ArrayList<String>();
            for (Items i : itemsOut.value)
                for (Item j : i.getItem())
                    productURLs.add(j.getDetailPageURL());

            ObjectNode node = Json.newObject();
            node.put("todo", Json.toJson(todo));
            node.put("productAdvertisingURLs", Json.toJson(productURLs));
            results.add(node);
        }

        return ok(Json.toJson(results));

        //return ok(getTodos.render(adv, ins, advr, AdventureTimeController.timeForm, AdventureFileController.fileForm));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result addTodo(String id) {

        DynamicForm requestData = form().bindFromRequest();

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        models.adventure.checklist.Todo todo = new models.adventure.checklist.Todo();
        todo.setAdventureId(id);
        todo.setUserId(usr.getId());
        todo.setTitle(requestData.get("title"));

        new TodoDAO().save(todo);

        ItemSearchRequest shared = new ItemSearchRequest();
        shared.setKeywords(todo.getTitle());
        shared.setSearchIndex("All");
        List<ItemSearchRequest> itemSearches = new ArrayList<ItemSearchRequest>();
        itemSearches.add(shared);


        Holder<OperationRequest> opOut = new Holder<OperationRequest>();
        Holder<List<Items>> itemsOut = new Holder<List<Items>>();

        //EMarketplaceDomain.US.getMarketPlaceDomain()
        AWSProductAdvertisingAPIHelper.getService().getAWSECommerceServicePort().itemSearch(null, ConfigFactory.load().getString("aws.productadvertising.accessKey"), "jourwheradvem-21", "Single", "False", shared, itemSearches, opOut, itemsOut);
        List<String> productURLs = new ArrayList<String>();
        for (Items i : itemsOut.value)
            for (Item j : i.getItem())
                productURLs.add(j.getDetailPageURL());

        ObjectNode result = Json.newObject();
        result.put("todo", Json.toJson(todo));
        result.put("productAdvertisingURLs", Json.toJson(productURLs));

        return ok(Json.toJson(result));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result setTodo(String id, String tid) {

        DynamicForm requestData = form().bindFromRequest();

        models.adventure.checklist.Todo todo = new TodoDAO().get(tid, id);

        String status = requestData.get("status").toUpperCase();
        todo.setStatus(EStatus.valueOf(status));

        new TodoDAO().save(todo);

        ItemSearchRequest shared = new ItemSearchRequest();
        shared.setKeywords(todo.getTitle());
        shared.setSearchIndex("All");
        List<ItemSearchRequest> itemSearches = new ArrayList<ItemSearchRequest>();
        itemSearches.add(shared);


        Holder<OperationRequest> opOut = new Holder<OperationRequest>();
        Holder<List<Items>> itemsOut = new Holder<List<Items>>();

        //EMarketplaceDomain.US.getMarketPlaceDomain()
        AWSProductAdvertisingAPIHelper.getService().getAWSECommerceServicePort().itemSearch(null, ConfigFactory.load().getString("aws.productadvertising.accessKey"), "jourwheradvem-21", "Single", "False", shared, itemSearches, opOut, itemsOut);
        List<String> productURLs = new ArrayList<String>();
        for (Items i : itemsOut.value)
            for (Item j : i.getItem())
                productURLs.add(j.getDetailPageURL());
        ObjectNode result = Json.newObject();
        result.put("todo", Json.toJson(todo));
        result.put("productAdvertisingURLs", Json.toJson(productURLs));

        return ok(Json.toJson(result)); //TODO: Error handling
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result deleteTodo(String id, String tid) {

        new TodoDAO().delete(tid, id);

        return ok(); //TODO: Error handling
    }

}
