package com.journwe.flight.skyscanner.query;

public class CheapestPriceByDateQuery extends BrowseCacheQuery {

	public CheapestPriceByDateQuery() {
		super();
		setBrowserCacheQueryType(BrowseCacheQueryType.BROWSE_DATES);
	}
}