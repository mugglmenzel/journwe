package com.journwe.flight.skyscanner.query.browsecache;

import com.journwe.flight.skyscanner.query.QueryType;

public class CheapestPriceByRouteQuery extends BrowseCacheQuery {

	public CheapestPriceByRouteQuery() {
		super();
		setQueryType(QueryType.BROWSE_ROUTES);
	}
}