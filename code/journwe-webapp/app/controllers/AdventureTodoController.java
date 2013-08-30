package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import com.journwe.productadvertising.webservice.client.Item;
import com.journwe.productadvertising.webservice.client.ItemSearchRequest;
import com.journwe.productadvertising.webservice.client.Items;
import com.journwe.productadvertising.webservice.client.OperationRequest;
import com.typesafe.config.ConfigFactory;
import models.adventure.checklist.EStatus;
import models.auth.SecuredBetaUser;
import models.dao.TodoDAO;
import models.dao.UserDAO;
import models.helpers.AWSProductAdvertisingAPIHelper;
import models.user.User;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.xml.ws.Holder;
import java.util.ArrayList;
import java.util.List;

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
        return ok(Json.toJson(new TodoDAO().all(userId, advId)));

        //return ok(getTodos.render(adv, ins, advr, AdventureTimeController.timeForm, AdventureFileController.fileForm));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getTodoAffiliateItems(String advId) {
        String id = form().bindFromRequest().get("id");
        models.adventure.checklist.Todo todo = new TodoDAO().get(id, advId);
        Logger.debug("searching affiliate for " + todo.getTitle());

        ItemSearchRequest shared = new ItemSearchRequest();
        shared.setKeywords(todo.getTitle());
        shared.setSearchIndex("All");
        shared.getResponseGroup().add("ItemAttributes");
        shared.getResponseGroup().add("Medium");
        shared.getResponseGroup().add("OfferSummary");
        shared.getResponseGroup().add("EditorialReview");
        List<ItemSearchRequest> itemSearches = new ArrayList<ItemSearchRequest>();
        itemSearches.add(shared);

        Holder<OperationRequest> opOut = new Holder<OperationRequest>();
        Holder<List<Items>> itemsOut = new Holder<List<Items>>();

        //EMarketplaceDomain.US.getMarketPlaceDomain()
        AWSProductAdvertisingAPIHelper.getService("ItemSearch").getAWSECommerceServicePort().itemSearch(null, ConfigFactory.load().getString("aws.productadvertising.accessKey"), "jourwheradvem-21", "Single", "False", shared, itemSearches, opOut, itemsOut);
        List<ObjectNode> productItems = new ArrayList<ObjectNode>();
        for (Items i : itemsOut.value)
            for (Item j : i.getItem()) {
                ObjectNode node = Json.newObject();
                node.put("url", j.getDetailPageURL());
                node.put("image", j.getMediumImage() != null ? j.getMediumImage().getURL() : (j.getLargeImage() != null ? j.getLargeImage().getURL() : ""));
                node.put("title", j.getItemAttributes().getTitle());
                node.put("description",  j.getEditorialReviews() != null && j.getEditorialReviews().getEditorialReview() != null && j.getEditorialReviews().getEditorialReview().size() > 0 && j.getEditorialReviews().getEditorialReview().get(0) != null ? j.getEditorialReviews().getEditorialReview().get(0).getContent() : "");
                node.put("price", j.getOfferSummary() != null && j.getOfferSummary().getLowestNewPrice() != null ? j.getOfferSummary().getLowestNewPrice().getFormattedPrice() : (j.getItemAttributes().getListPrice() != null ? j.getItemAttributes().getListPrice().getFormattedPrice() : ""));
                productItems.add(node);
            }


        Logger.debug("returning " + Json.toJson(productItems));
        return

                ok(Json.toJson(productItems));
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

        return ok(Json.toJson(todo));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result setTodo(String advId, String tid) {

        DynamicForm requestData = form().bindFromRequest();

        models.adventure.checklist.Todo todo = new TodoDAO().get(tid, advId);

        String status = requestData.get("status").toUpperCase();
        todo.setStatus(EStatus.valueOf(status));

        new TodoDAO().save(todo);

        return ok(Json.toJson(todo)); //TODO: Error handling
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result deleteTodo(String advId, String tid) {

        new TodoDAO().delete(tid, advId);

        return ok(); //TODO: Error handling
    }

}
