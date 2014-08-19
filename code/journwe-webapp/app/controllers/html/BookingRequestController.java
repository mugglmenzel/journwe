package controllers.html;

import models.adventure.Adventure;
import models.auth.SecuredUser;
import models.booking.BookingRequest;
import models.dao.adventure.AdventureDAO;
import org.joda.time.DateTime;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.booking.createBookingRequest;

import static play.data.Form.form;

/**
 * Created by markus on 18/08/14.
 */
public class BookingRequestController extends Controller {

    private static DynamicForm bookingDynamicForm = form();

    @Security.Authenticated(SecuredUser.class)
    public static Result createFromAdventure(String advId) {
        Form<BookingRequest> bookingFilledForm = form(BookingRequest.class);
        Adventure adv;
        if (advId != null && !"".equals(advId)) {
            BookingRequest bookingRequest = new BookingRequest();
            bookingRequest.setAdventureId(advId);
            bookingRequest.setTimestamp(new Long(DateTime.now().getMillis()));
            adv = new AdventureDAO().get(advId);
            bookingRequest.setAdventureName(adv.getName());
            bookingRequest.setAdditionalDescription("hello world");
            bookingFilledForm = bookingFilledForm.fill(bookingRequest);
        } else {
            bookingFilledForm = bookingFilledForm.fill(new BookingRequest());
            adv = new Adventure();
        }
        return ok(createBookingRequest.render(adv, bookingFilledForm));
    }


}
