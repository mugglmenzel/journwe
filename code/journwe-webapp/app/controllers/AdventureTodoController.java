package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import com.journwe.productadvertising.webservice.client.*;
import com.journwe.productadvertising.webservice.client.enums.EMarketplaceDomain;
import com.typesafe.config.ConfigFactory;
import models.Inspiration;
import models.adventure.checklist.Todo;
import models.auth.SecuredBetaUser;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.checklist.EStatus;
import models.dao.*;
import models.user.User;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.getTodos;

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
    public static Result getTodos(String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventure adv = new AdventureDAO().get(advId);
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        Inspiration ins = Inspiration.fromId(adv.getInspirationId());
        ins = ins != null ? new InspirationDAO().get(ins.getCategoryId(), ins.getInspirationId()) : ins;



        for(models.adventure.checklist.Todo todo : new TodoDAO().all(advId)) {
            ItemSearchRequest shared = new ItemSearchRequest();
            shared.setKeywords(todo.getTitle());
            Holder<OperationRequest> opOut = new Holder<OperationRequest>();
            Holder<List<Items>> itemsOut = new Holder<List<Items>>();
            new AWSECommerceService().getAWSECommerceServicePort().itemSearch(EMarketplaceDomain.US.getMarketPlaceDomain(), ConfigFactory.load().getString("aws.accessKey"), "jouaufinsabe-21", "Single", "False", shared, new ArrayList<ItemSearchRequest>(), opOut, itemsOut);
        }

        return ok(getTodos.render(adv, ins, advr, AdventureTimeController.timeForm, AdventureFileController.fileForm));
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
    public static Result setTodo(String id, String tid) {

        DynamicForm requestData = form().bindFromRequest();

        models.adventure.checklist.Todo todo = new TodoDAO().get(tid, id);

        String status = requestData.get("status").toUpperCase();
        todo.setStatus(EStatus.valueOf(status));

        new TodoDAO().save(todo);

        return ok(Json.toJson(todo)); //TODO: Error handling
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result deleteTodo(String id, String tid) {

        new TodoDAO().delete(tid, id);

        return ok(); //TODO: Error handling
    }

}
