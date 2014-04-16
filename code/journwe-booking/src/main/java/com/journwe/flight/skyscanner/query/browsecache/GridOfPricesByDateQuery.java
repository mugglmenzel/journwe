package com.journwe.flight.skyscanner.query.browsecache;

import com.journwe.flight.skyscanner.query.QueryType;

public class GridOfPricesByDateQuery extends BrowseCacheQuery {

	public GridOfPricesByDateQuery() {
		super();
		setQueryType(QueryType.BROWSE_GRID);
	}
}