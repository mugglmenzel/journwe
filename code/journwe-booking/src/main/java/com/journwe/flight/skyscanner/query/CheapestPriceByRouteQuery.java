package com.journwe.flight.skyscanner.query;

public class CheapestPriceByRouteQuery extends BrowseCacheQuery {

	public CheapestPriceByRouteQuery() {
		super();
		setBrowserCacheQueryType(BrowseCacheQueryType.BROWSE_ROUTES);
	}
}