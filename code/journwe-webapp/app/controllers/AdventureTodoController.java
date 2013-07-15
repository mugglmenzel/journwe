package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
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

        return ok(getTodos.render(adv, new InspirationDAO().get(adv.getInspirationId()), advr, AdventureTimeController.timeForm));
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
