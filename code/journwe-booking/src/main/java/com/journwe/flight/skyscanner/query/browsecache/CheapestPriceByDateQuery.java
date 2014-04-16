package com.journwe.flight.skyscanner.query.browsecache;

import com.journwe.flight.skyscanner.query.QueryType;

public class CheapestPriceByDateQuery extends BrowseCacheQuery {

	public CheapestPriceByDateQuery() {
		super();
		setQueryType(QueryType.BROWSE_DATES);
	}
}