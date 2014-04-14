package com.journwe.flight.skyscanner.query;

public class GridOfPricesByDateQuery extends BrowseCacheQuery {

	public GridOfPricesByDateQuery() {
		super();
		setBrowserCacheQueryType(BrowseCacheQueryType.BROWSE_GRID);
	}
}