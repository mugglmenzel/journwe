package com.journwe.flight.skyscanner;

import com.journwe.flight.skyscanner.query.BrowseCacheQuery;

public class QueryGenerator {

	public static final String BASE_URL = "http://partners.api.skyscanner.net/apiservices/";

	/**
	 * Generate a URL for REST request.
	 * 
	 * @param hotelRequest
	 * @return
	 */
	public static String generateUrl(final String apiKey,
			final BrowseCacheQuery browserCacheQuery) {
		StringBuffer toReturn = new StringBuffer();
		toReturn.append(BASE_URL);
		toReturn.append(browserCacheQuery.getBrowserCacheQuerytType().value());
		toReturn.append(browserCacheQuery.getCountry());
		toReturn.append("/");
		toReturn.append(browserCacheQuery.getCurrency());
		toReturn.append("/");
		toReturn.append(browserCacheQuery.getLocale());
		toReturn.append("/");
		toReturn.append(generateUrlPath(browserCacheQuery));
		toReturn.append("?apiKey=");
		toReturn.append(apiKey);
		return toReturn.toString();
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
