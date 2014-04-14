package com.journwe.hotel.expedia.model.sort;

/**
 * 
 * NO_SORT Used only in conjunction with hotelIdList. Returns hotels in the
 * exact order listed in the request.
 * 
 * CITY_VALUE The default sort order - returns hotels in the same order as if
 * sort is omitted entirely. Properties within the specified city are ordered
 * above properties in surrounding areas.
 * 
 * OVERALL_VALUE Places preferred, best-converting properties at the top.
 * 
 * PROMO Places properties with a promo rate or value add above properties not
 * running promotions. PRICE Sorts properties by nightly rate from low to high.
 * The ordering is not perfect due to business/marketing office algorithms
 * applied to property lists accessed by the API. Accurate price sorting is best
 * achieved within your own code after results are received.
 * 
 * PRICE_REVERSE Sorts properties by nightly rate from high to low. Expect
 * imperfect sort as detailed above.
 * 
 * PRICE_AVERAGE Sorts properties by average nightly rate from low to high.
 * Expect imperfect sort as detailed above.
 * 
 * QUALITY Sorts by property star rating from high to low.
 * 
 * QUALITY_REVERSE Sorts by property star rating from low to high.
 * 
 * ALPHA Sorts properties alphabetically
 * 
 * PROXIMITY Sorts based on proximity to the origin point defined via latitude &
 * longitude parameters.
 * 
 * POSTAL_CODE Sorts via postal code, from alphanumerically lower codes to
 * higher codes.
 * 
 * CONFIDENCE Hotel Collect properties only. Sorts via our collections
 * department's assessment of how likely individual properties are to pay
 * commissions and how quickly they pay them. Best-performing properties return
 * first.
 * 
 * MARKETING_CONFIDENCE Places Expedia Collect properties first, but sorts Hotel
 * Collect properties thereafter by their confidence level.
 * 
 * TRIP_ADVISOR If you have an approved TripAdvisor integration, this value will
 * sort results from high to low guest ratings.
 * 
 */
public enum SortType {

	NO_SORT, CITY_VALUE, OVERALL_VALUE, PROMO, PRICE_REVERSE, PRICE_AVERAGE, QUALITY, 
	QUALITY_REVERSE, ALPHA, PROXIMITY, POSTAL_CODE, CONFIDENCE, MARKETING_CONFIDENCE, 
	TRIP_ADVISOR;

}
