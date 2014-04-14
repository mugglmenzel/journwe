package com.journwe.flight.skyscanner.query;

import com.journwe.flight.skyscanner.model.location.DestinationPlace;
import com.journwe.flight.skyscanner.model.location.OriginPlace;
import com.journwe.flight.skyscanner.model.time.InboundPartialDate;
import com.journwe.flight.skyscanner.model.time.OutboundPartialDate;

public class BrowseCacheQueryBuilder {
	
	private BrowseCacheQuery query = null;
	
	public BrowseCacheQueryBuilder(final BrowseCacheQuery query) {
		this.query = query;
	}
	
	public BrowseCacheQueryBuilder withOriginPlace(OriginPlace originPlace) {
		query.setOriginPlace(originPlace);
		return this;
	}
	
	public BrowseCacheQueryBuilder withDestinationPlace(DestinationPlace destinationPlace) {
		query.setDestinationPlace(destinationPlace);
		return this;
	}
	
	public BrowseCacheQueryBuilder withOutboundPartialDate(OutboundPartialDate outboundPartialDate) {
		query.setOutboundPartialDate(outboundPartialDate);
		return this;
	}
	
	public BrowseCacheQueryBuilder withInboundPartialDate(InboundPartialDate inboundPartialDate) {
		query.setInboundPartialDate(inboundPartialDate);
		return this;
	}
	
	public BrowseCacheQuery build() {
		return query;
	}

}
