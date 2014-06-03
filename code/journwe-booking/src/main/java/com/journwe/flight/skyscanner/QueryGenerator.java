package com.journwe.flight.skyscanner;

import com.journwe.flight.skyscanner.query.Query;
import com.journwe.flight.skyscanner.query.browsecache.BrowseCacheQuery;
import com.journwe.flight.skyscanner.query.livepricing.BookingDetailsQuery;
import com.journwe.flight.skyscanner.query.livepricing.ListFlightsQuery;
import com.journwe.flight.skyscanner.query.livepricing.LivePricingCreateBookingDetailsQuery;
import com.journwe.flight.skyscanner.query.livepricing.LivePricingPollBookingDetailsQuery;
import com.journwe.flight.skyscanner.query.livepricing.LivePricingPollSessionQuery;

public class QueryGenerator {

	public static final String BASE_URL = "http://partners.api.skyscanner.net/apiservices/";

	public static String generateEndpoint(final String apiKey, final Query query) {
		if(query instanceof LivePricingPollSessionQuery) {
			LivePricingPollSessionQuery lppquery = (LivePricingPollSessionQuery)query;
			StringBuffer queryString = new StringBuffer();
			queryString.append(lppquery.getSessionUrl());
			queryString.append("?apiKey=");
			queryString.append(apiKey);
			queryString.append(lppquery.getQueryString());
			return queryString.toString();
		}
		if(query instanceof BookingDetailsQuery) {
			BookingDetailsQuery bdq = (BookingDetailsQuery)query;
			StringBuffer queryString = new StringBuffer();
			queryString.append(bdq.getSessionUrl());
			queryString.append("/booking");
//			queryString.append("?apiKey=");
//			queryString.append(apiKey);
			// this is encoded in the body of the HTTP PUT
//			if(query instanceof LivePricingCreateBookingDetailsQuery) {
//				LivePricingCreateBookingDetailsQuery cbdq = (LivePricingCreateBookingDetailsQuery)query;
//				queryString.append(cbdq.getQueryString());
//			}
			if(query instanceof LivePricingPollBookingDetailsQuery) {
				LivePricingPollBookingDetailsQuery pbdq = (LivePricingPollBookingDetailsQuery)query;
				queryString = new StringBuffer();
				queryString.append(pbdq.getBookingPollUrl());
				queryString.append("?apiKey=");
				queryString.append(apiKey);
			}
			return queryString.toString();
		}
		StringBuffer toReturn = new StringBuffer();
		toReturn.append(BASE_URL);
		toReturn.append(query.getQuerytType().value());
		if(query instanceof BrowseCacheQuery) {
			toReturn.append("/");
			toReturn.append(query.getCountry().getCode());
			toReturn.append("/");
			toReturn.append(query.getCurrency().getCode());
			toReturn.append("/");
			toReturn.append(query.getLocale().getCode());
			toReturn.append("/");
			BrowseCacheQuery bcquery = (BrowseCacheQuery)query;
			toReturn.append(generateUrlPath(bcquery));
			toReturn.append("?apiKey=");
			toReturn.append(apiKey);
		}
		return toReturn.toString();
	}
	
	// TODO refactor this
	public static String generateHttpPostBody(final String apiKey, final ListFlightsQuery query) {
		String toReturn = "";
		String country = query.getCountry().getCode();
		String currency = query.getCurrency().getCode();
		String locale = query.getLocale().getCode();
		String origin = query.getOriginPlace().getValue();
		String destination = query.getDestinationPlace().getValue();
		String outboundDate = query.getOutboundPartialDate()
				.getValue();
		String inboundDate = query.getInboundPartialDate()
				.getValue();
		toReturn = String.format("apikey=%s&country=%s&currency=%s&locale=%s&originplace=%s&destinationplace=%s&outbounddate=%s&inbounddate=%s&locationschema=Iata", apiKey, country, currency, locale, origin, destination,
				outboundDate, inboundDate);
		return toReturn;
	}
	
	// TODO refactor this
	public static String generateHttpPutBody(final String apiKey, final LivePricingCreateBookingDetailsQuery query) {
		String toReturn = "";
		String outboundlegId = query.getOutboundlegId();
		String inboundlegId = query.getInboundlegId();
		toReturn = String.format("apikey=%s&outboundlegid=%s&inboundlegid=%s", apiKey, outboundlegId, inboundlegId);
		return toReturn;
	}

	private static String generateUrlPath(
			final BrowseCacheQuery browserCacheQuery) {
		String toReturn = "";
		/**
		 * GET URL Format
		 * 
		 * http://partners.api.skyscanner.net/apiservices/browsequotes/v1.0/{
		 * country}/{currency}/{locale}/{originPlace}/{destinationPlace}/{
		 * outboundPartialDate}/{inboundPartialDate}?apiKey={apiKey}
		 */
		String origin = browserCacheQuery.getOriginPlace().getValue();
		String destination = browserCacheQuery.getDestinationPlace().getValue();
		String outboundDate = browserCacheQuery.getOutboundPartialDate()
				.getValue();
		String inboundDate = browserCacheQuery.getInboundPartialDate()
				.getValue();
		toReturn = String.format("%s/%s/%s/%s", origin, destination,
				outboundDate, inboundDate);
		return toReturn;
	}
}
