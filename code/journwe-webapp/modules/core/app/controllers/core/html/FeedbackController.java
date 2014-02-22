package controllers.core.html;

import com.feth.play.module.pa.PlayAuthenticate;
import models.UserManager;
import models.contact.EFeedbackType;
import models.contact.Feedback;
import models.auth.SecuredUser;
import models.dao.FeedbackDAO;
import models.user.User;
import play.data.DynamicForm;
import play.mvc.Http;
import play.mvc.Security;

import java.util.Date;

import static play.data.Form.form;
import static play.mvc.Controller.flash;
import static play.mvc.Http.HeaderNames.REFERER;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class FeedbackController {

    @Security.Authenticated(SecuredUser.class)
    public static play.mvc.Result createFeedback() {
        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        DynamicForm data = form().bindFromRequest();
        // Save in our DynamoDB database
        Feedback feedback = new Feedback();
        String feedbackTypeString = data.get("feedbackType");
        feedback.setFeedbackType(EFeedbackType.valueOf(feedbackTypeString));
        feedback.setText(data.get("text"));
        feedback.setTimeStamp(new Date());
        feedback.setUserId(usr.getId());
        feedback.setUserName(usr.getName());
        new FeedbackDAO().save(feedback);
        flash("success","Thanks!! Your feedback has been submitted successfully!");

        return redirect(Http.Context.current().request().getHeader(REFERER));
    }

}
