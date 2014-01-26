package controllers.html;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.Adventure;
import models.adventure.adventurer.Adventurer;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
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

        // Retrieve the Adventure from the database
        Adventure adv = new AdventureDAO().get(advId);
        // Adventure name => PDF title
        String advName = adv.getName();
        // Create PDF document
        PDDocument document = PdfboxService.create(advName);

        // Include place in PDF
        if(pdfPlaceChecked) {
            // Clone place options
            for(PlaceOption po : new PlaceOptionDAO().all(advId)) {
                String placeOptionAddress = po.getAddress();
                // TODO add to PDF
            }
        }

        // Save the file in ByteArrayOutputStream
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfboxService.save(document, output);

        response().setContentType("application/x-download");
        response().setHeader("Content-Disposition", "attachment; filename=\""+advName+".pdf\"");
        ByteArrayInputStream bais = new ByteArrayInputStream(output.toByteArray());
        return ok(bais);
    }

}
