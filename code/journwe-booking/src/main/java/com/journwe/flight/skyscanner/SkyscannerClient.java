package com.journwe.flight.skyscanner;

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
import com.journwe.flight.skyscanner.model.location.DestinationPlace;
import com.journwe.flight.skyscanner.model.location.LocationType;
import com.journwe.flight.skyscanner.model.location.OriginPlace;
import com.journwe.flight.skyscanner.model.time.InboundPartialDate;
import com.journwe.flight.skyscanner.model.time.OutboundPartialDate;
import com.journwe.flight.skyscanner.query.BrowseCacheQuery;
import com.journwe.flight.skyscanner.query.BrowseCacheQueryBuilder;
import com.journwe.flight.skyscanner.query.CheapestPriceByDateQuery;
import com.journwe.flight.skyscanner.query.CheapestPriceByRouteQuery;
import com.journwe.flight.skyscanner.query.CheapestQuoteQuery;
import com.journwe.flight.skyscanner.query.GridOfPricesByDateQuery;

public class SkyscannerClient {

	private static final String API_KEY = "jo485734908506548900205643098517";

	// Create an instance of HttpClient.
	private static HttpClient client = new HttpClient();

	public static void main(String[] args) throws ParseException {
		// Create a CheapestQuoteQuery
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date june10 = sdf.parse("10/06/2014");
		Date june15 = sdf.parse("15/06/2014");
		BrowseCacheQuery cheapestQuoteQuery = new BrowseCacheQueryBuilder(new CheapestQuoteQuery())
				.withOriginPlace(new OriginPlace("UK"))
				.withDestinationPlace(
						new DestinationPlace(LocationType.ANYWHERE.value()))
				.withOutboundPartialDate(new OutboundPartialDate(june10))
				.withInboundPartialDate(new InboundPartialDate(june15)).build();
		JsonNode jsonNode = query(cheapestQuoteQuery);
		System.out.println(jsonNode);
		
		/**
		// Create a CheapestPriceByDateQuery
		BrowseCacheQuery cheapestPriceByDateQuery = new BrowseCacheQueryBuilder(new CheapestPriceByDateQuery())
				.withOriginPlace(new OriginPlace("UK"))
				.withDestinationPlace(
						new DestinationPlace(LocationType.ANYWHERE.value()))
				.withOutboundPartialDate(new OutboundPartialDate(june10))
				.withInboundPartialDate(new InboundPartialDate(june15)).build();
		jsonNode = query(cheapestPriceByDateQuery);
		System.out.println(jsonNode);
		
		// Create a CheapestPriceByRouteQuery
		BrowseCacheQuery cheapestPriceByRouteQuery = new BrowseCacheQueryBuilder(new CheapestPriceByRouteQuery())
				.withOriginPlace(new OriginPlace("UK"))
				.withDestinationPlace(
						new DestinationPlace(LocationType.ANYWHERE.value()))
				.withOutboundPartialDate(new OutboundPartialDate(june10))
				.withInboundPartialDate(new InboundPartialDate(june15)).build();
		jsonNode = query(cheapestPriceByRouteQuery);
		System.out.println(jsonNode);
		
		// Create a GridOfPricesByDateQuery
		BrowseCacheQuery gridOfBrowseCacheQuery = new BrowseCacheQueryBuilder(new GridOfPricesByDateQuery())
				.withOriginPlace(new OriginPlace("UK"))
				.withDestinationPlace(
						new DestinationPlace(LocationType.ANYWHERE.value()))
				.withOutboundPartialDate(new OutboundPartialDate(june10))
				.withInboundPartialDate(new InboundPartialDate(june15)).build();
		jsonNode = query(gridOfBrowseCacheQuery);
		System.out.println(jsonNode);
		**/
	}

	public static JsonNode query(final BrowseCacheQuery browserCacheQuery) {
		String url = QueryGenerator.generateUrl(API_KEY, browserCacheQuery);
		System.out.println("HTTP GET " + url);
		GetMethod method = new GetMethod(url);
		// method.setRequestHeader("Accept", "application/json");

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