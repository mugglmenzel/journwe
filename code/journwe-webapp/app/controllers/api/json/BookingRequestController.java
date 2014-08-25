package controllers.api.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.adventure.Adventure;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.auth.SecuredUser;
import models.booking.BookingRequest;
import models.dao.AdventureShortnameDAO;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.PlaceOptionDAO;
import models.dao.adventure.TimeOptionDAO;
import models.dao.booking.BookingRequestDAO;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.ok;

public class BookingRequestController {

    private static final String ADVENTURE_ID = "adventureId";
    private static final String ADVENTURE_NAME = "bookingRequest-adventureName";
    private static final String FAVORITE_PLACE_ADDRESS = "bookingRequest-favoritePlaceAddress";
    private static final String FAVORITE_PLACE_DESCRIPTION = "bookingRequest-favoritePlaceDescription";
    private static final String FAVORITE_START_DATE = "bookingRequest-favoriteStartDate";
    private static final String FAVORITE_END_DATE = "bookingRequest-favoriteEndDate";
    private static final String ADDITIONAL_DESCRIPTION = "bookingRequest-additionalDescription";

    private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    @Security.Authenticated(SecuredUser.class)
    public static Result createBookingRequestFromAdventure(String advId) {
        Logger.debug("Create booking request from adventure "+advId);
        Adventure adv;
        if (advId != null && !"".equals(advId) && !"undefined".equals(advId)) {
            // Read adventure attributes
            adv = new AdventureDAO().get(advId);
            String advName = adv.getName();
            String favPlaceId = adv.getFavoritePlaceId();
            String favTimeId = adv.getFavoriteTimeId();
            PlaceOption favPlace = new PlaceOptionDAO().get(advId, favPlaceId);
            TimeOption favTime = new TimeOptionDAO().get(advId, favTimeId);
            // Create booking request
            BookingRequest bookingRequest = new BookingRequest();
            bookingRequest.setAdventureId(advId);
            bookingRequest.setTimestamp(new Long(DateTime.now().getMillis()));
            // Add booking request attributes
            bookingRequest.setAdventureName(advName);
            bookingRequest.setFavoritePlaceAddress(favPlace.getAddress());
            bookingRequest.setFavoritePlaceDescription(favPlace.getDescription());
            bookingRequest.setFavoriteStartDate(favTime.getStartDate());
            bookingRequest.setFavoriteEndDate(favTime.getEndDate());
            bookingRequest.setAdditionalDescription("hello world");
            // Save booking request in database
            BookingRequestDAO dao = new BookingRequestDAO();
            dao.save(bookingRequest);
        } else {
            Logger.error("Wanted to create a booking request from an adventure but failed because adventure id is null or empty.");
            return internalServerError();
        }
        // return the newly saved object
        return getBookingRequest(advId);
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getBookingRequest(String adventureId) {
        BookingRequest br = loadNewestBookingRequest(adventureId);
        ObjectNode node = getFrom(br);
        Logger.debug(node.toString());
        return ok(Json.toJson(node));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result saveBookingRequest(String adventureId) {
        DynamicForm bookingRequestDraft = form().bindFromRequest();
        try {
            BookingRequest br = getFrom(adventureId, bookingRequestDraft);
            if(new BookingRequestDAO().save(br)) {
                ObjectNode node = getFrom(br);
                Logger.debug(node.toString());
                return ok(Json.toJson(node));
            } else {
                Logger.warn("Saving booking request with adventure id "+br.getAdventureId()+" and timestamp "+br.getTimestamp()+" has failed!");
                return internalServerError();
            }
        } catch (ParseException e) {
            Logger.error(e.getMessage());
            return internalServerError();
        }
    }

    /**
     * Helper method.
     *
     * @param adventureId
     * @return
     */
    private static BookingRequest loadNewestBookingRequest(String adventureId) {
        BookingRequest toReturn = null;
        BookingRequestDAO dao = new BookingRequestDAO();
        List<BookingRequest> bookingRequests = dao.all();
        for(BookingRequest br: bookingRequests) {
              if(toReturn==null)
                  toReturn = br;
              if(br.getTimestamp() > toReturn.getTimestamp())
                  toReturn = br;
        }
        return toReturn;
    }

    /**
     * Helper method.
     *
     * @param bookingRequest
     * @return
     */
    private static ObjectNode getFrom(BookingRequest bookingRequest) {
        ObjectNode node = Json.newObject();
        node.put(ADVENTURE_NAME, bookingRequest.getAdventureName());
        node.put(FAVORITE_PLACE_ADDRESS, bookingRequest.getFavoritePlaceAddress());
        node.put(FAVORITE_PLACE_DESCRIPTION, bookingRequest.getFavoritePlaceDescription());
        String favoriteStartDate = DATE_FORMATTER.format(bookingRequest.getFavoriteStartDate());
        node.put(FAVORITE_START_DATE, favoriteStartDate);
        String favoriteEndDate = DATE_FORMATTER.format(bookingRequest.getFavoriteEndDate());
        node.put(FAVORITE_END_DATE, favoriteEndDate);
        node.put(ADDITIONAL_DESCRIPTION, bookingRequest.getAdditionalDescription());
        return node;
    }

    /**
     * Helper method.
     *
     * @param myForm
     * @return
     * @throws ParseException
     */
    private static BookingRequest getFrom(String advId, DynamicForm myForm) throws ParseException {
        if(advId==null || advId.isEmpty()) {
            Logger.error("Transforming dynamic form into BookingRequest object failed because adventure id was not supplied.");
            return new BookingRequest();
        }
        BookingRequest toReturn = loadNewestBookingRequest(advId);
        toReturn.setAdventureName(myForm.get(ADVENTURE_NAME));
        toReturn.setFavoritePlaceAddress(myForm.get(FAVORITE_PLACE_ADDRESS));
        toReturn.setFavoritePlaceDescription(myForm.get(FAVORITE_PLACE_DESCRIPTION));
        Date favoriteStartDate = DATE_FORMATTER.parse(myForm.get(FAVORITE_START_DATE));
        toReturn.setFavoriteStartDate(favoriteStartDate);
        Date favoriteEndDate = DATE_FORMATTER.parse(myForm.get(FAVORITE_END_DATE));
        toReturn.setFavoriteEndDate(favoriteEndDate);
        toReturn.setAdditionalDescription(myForm.get(ADDITIONAL_DESCRIPTION));
        return toReturn;
    }
}
