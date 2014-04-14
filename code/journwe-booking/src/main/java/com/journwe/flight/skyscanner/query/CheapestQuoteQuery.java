package com.journwe.flight.skyscanner.query;


public class CheapestQuoteQuery extends BrowseCacheQuery {

	public CheapestQuoteQuery() {
		super();
		setBrowserCacheQueryType(BrowseCacheQueryType.BROWSE_QUOTES);
	}

}
