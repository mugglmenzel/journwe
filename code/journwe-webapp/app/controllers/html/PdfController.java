package controllers.html;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.Adventure;
import models.adventure.adventurer.Adventurer;
import models.adventure.email.Message;
import models.adventure.file.JournweFile;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.adventure.todo.Todo;
import models.auth.SecuredUser;
import models.dao.adventure.*;
import models.dao.user.UserDAO;
import models.user.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import services.PdfboxService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;
import static play.mvc.Http.Context.Implicit.response;
import static play.mvc.Results.ok;

/**
 * Created by markus on 26/01/14.
 */
public class PdfController {

    private static DynamicForm pdfForm = form();

    @Security.Authenticated(SecuredUser.class)
    public static Result create(final String advId) {
        // The Dynamic Form has the checkboxes
        // which adventure components should be printed as PDF.
        DynamicForm filledForm = pdfForm.bindFromRequest();
        Boolean pdfPlaceChecked = new Boolean(filledForm.get("pdfPlace"));
        Boolean pdfTimeChecked = new Boolean(filledForm.get("pdfTime"));
        Boolean pdfTodoChecked = new Boolean(filledForm.get("pdfTodo"));
        Boolean pdfEmailChecked = new Boolean(filledForm.get("pdfEmail"));
        Boolean pdfFileChecked = new Boolean(filledForm.get("pdfFile"));
        // This user
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        String usrId = usr.getId();
        // Fill these objects with data
        PlaceOption placeOption = null;
        TimeOption timeOption = null;
        List<Todo> todos = null;
        List<Message> emails = null;
        List<JournweFile> files = null;

        // Retrieve the Adventure from the database
        Adventure adv = new AdventureDAO().get(advId);
        // Adventure name => PDF title
        String advName = adv.getName();

        // Include place in PDF
        if(pdfPlaceChecked) {
            String favPlaceId = adv.getFavoritePlaceId();
            placeOption = new PlaceOptionDAO().get(advId,favPlaceId);
        }

        // Include time in PDF
        if(pdfTimeChecked) {
            String favTimeId = adv.getFavoriteTimeId();
            timeOption = new TimeOptionDAO().get(advId,favTimeId);
        }

        // Include TODOlist in PDF
        if(pdfTodoChecked) {
            todos = new TodoDAO().all(usrId,advId);
        }

        // Create PDF document
        PDDocument document = PdfboxService.create(advName, placeOption, timeOption, todos);
        // Save the file in ByteArrayOutputStream
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfboxService.save(document, output);

        // Include Emails in PDF
        if(pdfEmailChecked) {
            emails = new AdventureEmailMessageDAO().all(advId);
            output= PdfboxService.appendEmailsToPDF(output,emails);
        }

        // Include Files in PDF
        if(pdfFileChecked) {
            files = new JournweFileDAO().all(advId);
            output= PdfboxService.appendFilesToPDF(output,files);
        }

        // Prepare response and return file for download.
        response().setContentType("application/x-download");
        response().setHeader("Content-Disposition", "attachment; filename=\""+advName+".pdf\"");
        ByteArrayInputStream bais = new ByteArrayInputStream(output.toByteArray());
        return ok(bais);
    }

}
