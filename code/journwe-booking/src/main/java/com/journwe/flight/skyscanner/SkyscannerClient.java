package com.journwe.flight.skyscanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.journwe.flight.skyscanner.model.filter.MaxNumberOfStops;
import com.journwe.flight.skyscanner.model.location.DestinationPlace;
import com.journwe.flight.skyscanner.model.location.OriginPlace;
import com.journwe.flight.skyscanner.model.time.InboundPartialDate;
import com.journwe.flight.skyscanner.model.time.OutboundPartialDate;
import com.journwe.flight.skyscanner.query.Query;
import com.journwe.flight.skyscanner.query.QueryBuilder;
import com.journwe.flight.skyscanner.query.livepricing.ListFlightsQuery;
import com.journwe.flight.skyscanner.query.livepricing.LivePricingPollQuery;

public class SkyscannerClient {

	private static final String API_KEY = "jo485734908506548900205643098517";

	// Create an instance of HttpClient.
	private static HttpClient client = new HttpClient();

	public static void main(String[] args) throws ParseException {

		// Create a LivePricingQuery
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date june10 = sdf.parse("10/06/2014");
		Date june15 = sdf.parse("15/06/2014");
		ListFlightsQuery listFlightsQuery = (ListFlightsQuery) new QueryBuilder(
				new ListFlightsQuery()).withOriginPlace(new OriginPlace("EDI"))
				.withDestinationPlace(new DestinationPlace("LHR"))
				.withOutboundPartialDate(new OutboundPartialDate(june10))
				.withInboundPartialDate(new InboundPartialDate(june15)).build();
		String sessionUrl = makeHttpPostQuery(listFlightsQuery);
		System.out.println("Session: " + sessionUrl);

		// String sessionUrl =
		// "http://partners.api.skyscanner.net/apiservices/pricing/v1.0/40461c50d91c45a396631b777a012121_elhhpiln_B6D8CE1B67A7A30D5CF190D260853651";
		LivePricingPollQuery livePricingPollQuery = new LivePricingPollQuery(
				sessionUrl).withMaxNumberOfStops(new MaxNumberOfStops(0));
		JsonNode jsonNode = makeHttpGetQuery(livePricingPollQuery);
		System.out.println(jsonNode);

		/**
		 * // Create a CheapestQuoteQuery SimpleDateFormat sdf = new
		 * SimpleDateFormat("dd/MM/yyyy"); Date june10 =
		 * sdf.parse("10/06/2014"); Date june15 = sdf.parse("15/06/2014");
		 * BrowseCacheQuery cheapestQuoteQuery = new BrowseCacheQueryBuilder(new
		 * CheapestQuoteQuery()) .withOriginPlace(new OriginPlace("UK"))
		 * .withDestinationPlace( new
		 * DestinationPlace(LocationType.ANYWHERE.value()))
		 * .withOutboundPartialDate(new OutboundPartialDate(june10))
		 * .withInboundPartialDate(new InboundPartialDate(june15)).build();
		 * JsonNode jsonNode = query(cheapestQuoteQuery);
		 * System.out.println(jsonNode);
		 * 
		 * 
		 * // Create a CheapestPriceByDateQuery BrowseCacheQuery
		 * cheapestPriceByDateQuery = new BrowseCacheQueryBuilder(new
		 * CheapestPriceByDateQuery()) .withOriginPlace(new OriginPlace("UK"))
		 * .withDestinationPlace( new
		 * DestinationPlace(LocationType.ANYWHERE.value()))
		 * .withOutboundPartialDate(new OutboundPartialDate(june10))
		 * .withInboundPartialDate(new InboundPartialDate(june15)).build();
		 * jsonNode = query(cheapestPriceByDateQuery);
		 * System.out.println(jsonNode);
		 * 
		 * // Create a CheapestPriceByRouteQuery BrowseCacheQuery
		 * cheapestPriceByRouteQuery = new BrowseCacheQueryBuilder(new
		 * CheapestPriceByRouteQuery()) .withOriginPlace(new OriginPlace("UK"))
		 * .withDestinationPlace( new
		 * DestinationPlace(LocationType.ANYWHERE.value()))
		 * .withOutboundPartialDate(new OutboundPartialDate(june10))
		 * .withInboundPartialDate(new InboundPartialDate(june15)).build();
		 * jsonNode = query(cheapestPriceByRouteQuery);
		 * System.out.println(jsonNode);
		 * 
		 * // Create a GridOfPricesByDateQuery BrowseCacheQuery
		 * gridOfBrowseCacheQuery = new BrowseCacheQueryBuilder(new
		 * GridOfPricesByDateQuery()) .withOriginPlace(new OriginPlace("UK"))
		 * .withDestinationPlace( new
		 * DestinationPlace(LocationType.ANYWHERE.value()))
		 * .withOutboundPartialDate(new OutboundPartialDate(june10))
		 * .withInboundPartialDate(new InboundPartialDate(june15)).build();
		 * jsonNode = query(gridOfBrowseCacheQuery);
		 * System.out.println(jsonNode);
		 **/
	}

	@SuppressWarnings("deprecation")
	public static String makeHttpPostQuery(final ListFlightsQuery query) {
		String url = QueryGenerator.generateEndpoint(API_KEY, query);
		System.out.println("HTTP POST " + url);
		PostMethod method = new PostMethod(url);
		String body = QueryGenerator.generateHttpPostBody(API_KEY, query);
		System.out.println("BODY: " + body);
		method.setRequestBody(body);
		method.setRequestHeader("Accept-Charset", "UTF-8");
		method.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");

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
			String sessionUrl = method.getResponseHeader("Location").getValue();
			return sessionUrl;

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

	public static JsonNode makeHttpGetQuery(final Query query) {
		String url = QueryGenerator.generateEndpoint(API_KEY, query);
		System.out.println("HTTP GET " + url);
		GetMethod method = new GetMethod(url);
		method.setRequestHeader("Accept", "application/json");
		method.setRequestHeader("Accept-Charset", "UTF-8");
		method.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");

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

	private static String streamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
}