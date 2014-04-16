package com.journwe.flight.skyscanner.query.browsecache;

import com.journwe.flight.skyscanner.query.QueryType;


public class CheapestQuoteQuery extends BrowseCacheQuery {

	public CheapestQuoteQuery() {
		super();
		setQueryType(QueryType.BROWSE_QUOTES);
	}

}
