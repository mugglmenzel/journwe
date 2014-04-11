package com.journwe.booking.expedia;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpediaClient {

	public static final String BASE_URL = "http://api.ean.com/ean-services/rs/hotel/v3/";
	public static final String MINOR_REV = "99";
    public static final String CID = "55505";
    public static final String API_KEY = "cr47t8qtx2ub9rk65usay3f6";
    



	public static void main(String[] args) throws ParseException {
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		RoomRequest roomRequest = new RoomRequest().withNumberOfAdults(2);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date arrivalDate = sdf.parse("10/06/2014");
		Date departureDate = sdf.parse("15/06/2014");
		HotelRequest hotelRequest = new HotelRequest(HotelRequestType.LIST).withRoomRequests(roomRequest).withCity("Seattle").withStateProvinceCode("WA").withCountryCode("US").withArrivalDate(arrivalDate).withDepartureDate(departureDate);
		String url = generateUrl(hotelRequest);
		System.out.println("HTTP GET "+url);
		GetMethod method = new GetMethod(url);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary
			// data
			System.out.println(new String(responseBody));

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}

	private static String generateUrl(final HotelRequest hotelRequest) {
		StringBuffer toReturn = new StringBuffer();
		toReturn.append(BASE_URL);
		toReturn.append(hotelRequest.getHotelRequestType().getRequestType());
		toReturn.append("?minorRev=");				
		toReturn.append(MINOR_REV);		
		toReturn.append("&apiKey=");
		toReturn.append(API_KEY);
		toReturn.append("&cid=");
		toReturn.append(CID);
		toReturn.append(hotelRequest.generateRequestParameters());
		return toReturn.toString();
	}
}