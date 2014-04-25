package com.journwe.flight.skyscanner.query.livepricing;

import com.journwe.flight.skyscanner.model.filter.DestinationAirports;
import com.journwe.flight.skyscanner.model.filter.MaxNumberOfStops;
import com.journwe.flight.skyscanner.model.filter.OriginAirports;
import com.journwe.flight.skyscanner.model.sort.SortOrder;
import com.journwe.flight.skyscanner.model.sort.SortType;

public class LivePricingPollQuery extends LivePricingQuery {

	private String sessionUrl;
	
	// sorting results
	private SortOrder sortOrder;
	private SortType sortType;
	
	// search filters
	private DestinationAirports destinationAirportsFilter;
	private OriginAirports originAirportsFilter;
	private MaxNumberOfStops maxNumberOfStopsFilter;

	public LivePricingPollQuery(final String sessionUrl) {
		this.sessionUrl = sessionUrl;
	}

	public String getSessionUrl() {
		return sessionUrl;
	}

	public void setSessionUrl(String sessionUrl) {
		this.sessionUrl = sessionUrl;
	}
	
	public LivePricingPollQuery withSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}
	
	public LivePricingPollQuery withSortType(SortType sortType) {
		this.sortType = sortType;
		return this;
	}
	
	public LivePricingPollQuery withDestinationAirportsFilter(DestinationAirports destinationAirports) {
		this.destinationAirportsFilter = destinationAirports;
		return this;
	}
	
	public LivePricingPollQuery withOriginAirports(OriginAirports originAirports) {
		this.originAirportsFilter = originAirports;
		return this;
	}
	
	public LivePricingPollQuery withMaxNumberOfStops(MaxNumberOfStops maxNumberOfStops) {
		this.maxNumberOfStopsFilter = maxNumberOfStops;
		return this;
	}


	/**
	 * returns all query parameters as &name1=value1&name2=value2 etc.
	 */
	public String getQueryString() {
		StringBuffer toReturn = new StringBuffer();
		if(sortOrder!=null)
			toReturn.append(sortOrder.getQueryString());
		if(sortType!=null)
			toReturn.append(sortType.getQueryString());
		if(destinationAirportsFilter!=null)
			toReturn.append(destinationAirportsFilter.getQueryString());
		if(originAirportsFilter!=null)
			toReturn.append(originAirportsFilter.getQueryString());
		if(maxNumberOfStopsFilter!=null)
			toReturn.append(maxNumberOfStopsFilter.getQueryString());
		return toReturn.toString();
	}

}
