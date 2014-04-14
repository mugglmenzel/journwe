package com.journwe.hotel.expedia;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.journwe.hotel.expedia.model.destination.City;
import com.journwe.hotel.expedia.model.destination.CountryCode;
import com.journwe.hotel.expedia.model.destination.StateProvinceCode;
import com.journwe.hotel.expedia.model.room.Room;
import com.journwe.hotel.expedia.model.room.Rooms;
import com.journwe.hotel.expedia.model.time.ArrivalDate;
import com.journwe.hotel.expedia.model.time.DepartureDate;

public class ExpediaClient {

	private static final String CID = "55505";
	// private static final String CID = "455210";
	private static final String API_KEY = "cr47t8qtx2ub9rk65usay3f6";
	private static final String SHARED_SECRET = "hrAA3KD9";

	// private static final String API_KEY = "uuph6fb6wv46taepq87bkwt7"; // BETA
	// private static final String SHARED_SECRET = "wCdUR4JY"; // BETA
	
	// Create an instance of HttpClient.
	private static HttpClient client = new HttpClient();

	public static void main(String[] args) throws ParseException {
		// Create a Hotel List request.
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date june10 = sdf.parse("10/06/2014");
		Date june15 = sdf.parse("15/06/2014");
		HotelListRequest hotelListRequest = new HotelRequestBuilder<HotelListRequest>(
				HotelListRequest.class).with(new City("Seattle"))
				.with(new CountryCode("US")).with(new StateProvinceCode("WA"))
				.with(new Rooms(new Room(2))).with(new ArrivalDate(june10))
				.with(new DepartureDate(june15)).build();
		JsonNode jsonNode = listHotels(hotelListRequest);
		System.out.println(jsonNode);
	}
	
	public static JsonNode listHotels(final HotelListRequest hotelListRequest) {
		String url = RequestGenerator.generateUrl(API_KEY, CID, SHARED_SECRET,
				hotelListRequest);
		System.out.println("HTTP GET " + url);
		GetMethod method = new GetMethod(url);
		//method.setRequestHeader("Accept", "application/json");

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
			InputStream is = method.getResponseBodyAsStream();
			JsonNode jsonNode = Json.parse(is);
			return jsonNode;

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
		return null;
	}
}