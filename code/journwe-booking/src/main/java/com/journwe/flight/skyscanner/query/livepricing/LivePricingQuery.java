package com.journwe.flight.skyscanner.query.livepricing;

import com.journwe.flight.skyscanner.query.Query;
import com.journwe.flight.skyscanner.query.QueryType;

public abstract class LivePricingQuery extends Query {

	public LivePricingQuery() {
		super();
		setQueryType(QueryType.PRICING);
	}
}
