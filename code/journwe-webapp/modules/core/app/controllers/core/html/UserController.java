package controllers.core.html;

import models.auth.SecuredUser;
import models.dao.user.UserDAO;
import models.dao.user.UserEmailDAO;
import models.user.User;
import models.user.UserEmail;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import views.html.user.get;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 26.07.13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public class UserController extends Controller {

    @Security.Authenticated(SecuredUser.class)
    public static Result getProfile(String userId) {
        return Results.ok(get.render(new UserDAO().get(userId)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result saveEditable() {
        DynamicForm usrForm = form().bindFromRequest();
        String usrId = usrForm.get("pk");
        if (usrId != null && !"".equals(usrId)) {
            User usr = new UserDAO().get(usrId);
            String name = usrForm.get("name");
            if ("userName".equals(name)) {
                usr.setName(usrForm.get("value"));
            }
            new UserDAO().save(usr);
        }

        return ok();
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result saveEmailEditable() {
        DynamicForm emailForm = form().bindFromRequest();
        String emailId = emailForm.get("pk");
        if (emailId != null && !"".equals(emailId)) {
            UserEmail email = new UserEmailDAO().get(emailId.substring(0, emailId.indexOf(":")), emailId.substring(emailId.indexOf(":")+1));
            UserEmail newEmail = new UserEmail();
            newEmail.setUserId(email.getUserId());
            newEmail.setEmail(email.getEmail());
            newEmail.setPrimary(email.isPrimary());
            newEmail.setValidated(email.isValidated());

            String name = emailForm.get("name");
            if (email != null && name.contains("userEmail")) {
                newEmail.setEmail(emailForm.get("value"));
                new UserEmailDAO().save(newEmail);
                new UserEmailDAO().delete(email.getUserId(), email.getEmail());
            }

        }

        return ok();
    }

}
