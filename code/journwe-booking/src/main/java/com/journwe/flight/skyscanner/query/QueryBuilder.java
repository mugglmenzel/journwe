package com.journwe.flight.skyscanner.query;

import com.journwe.flight.skyscanner.model.location.DestinationPlace;
import com.journwe.flight.skyscanner.model.location.OriginPlace;
import com.journwe.flight.skyscanner.model.time.InboundPartialDate;
import com.journwe.flight.skyscanner.model.time.OutboundPartialDate;

public class QueryBuilder {
	
	private Query query = null;
	
	public QueryBuilder(final Query query) {
		this.query = query;
	}
	
	public QueryBuilder withOriginPlace(OriginPlace originPlace) {
		query.setOriginPlace(originPlace);
		return this;
	}
	
	public QueryBuilder withDestinationPlace(DestinationPlace destinationPlace) {
		query.setDestinationPlace(destinationPlace);
		return this;
	}
	
	public QueryBuilder withOutboundPartialDate(OutboundPartialDate outboundPartialDate) {
		query.setOutboundPartialDate(outboundPartialDate);
		return this;
	}
	
	public QueryBuilder withInboundPartialDate(InboundPartialDate inboundPartialDate) {
		query.setInboundPartialDate(inboundPartialDate);
		return this;
	}
	
	public Query build() {
		return query;
	}

}
