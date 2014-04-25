package com.journwe.flight.skyscanner;

import com.journwe.flight.skyscanner.query.Query;
import com.journwe.flight.skyscanner.query.browsecache.BrowseCacheQuery;
import com.journwe.flight.skyscanner.query.livepricing.ListFlightsQuery;
import com.journwe.flight.skyscanner.query.livepricing.LivePricingPollQuery;

public class QueryGenerator {

	public static final String BASE_URL = "http://partners.api.skyscanner.net/apiservices/";

	public static String generateEndpoint(final String apiKey, final Query query) {
		if(query instanceof LivePricingPollQuery) {
			LivePricingPollQuery lppquery = (LivePricingPollQuery)query;
			StringBuffer queryString = new StringBuffer();
			queryString.append(lppquery.getSessionUrl());
			queryString.append("?apiKey=");
			queryString.append(apiKey);
			queryString.append(lppquery.getQueryString());
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
