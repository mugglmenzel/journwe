package models.dao.booking;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.booking.BookingRequest;
import models.dao.common.CommonEntityDAO;

import java.util.List;

public class BookingRequestDAO extends CommonEntityDAO<BookingRequest> {

    public BookingRequestDAO() {
        super(BookingRequest.class);
    }

    public List<BookingRequest> all() {
        return pm.scan(BookingRequest.class,
                new DynamoDBScanExpression());
    }

}
