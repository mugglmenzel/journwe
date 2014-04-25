package com.journwe.flight.skyscanner.model.filter;

public class DestinationAirports extends Airports {
	
	@Override
	public String getValue() {
		return getAirportCodes();
	}

	public String getQueryString() {
		return "&destinationairports="+this.getAirportCodes();
	}
	
}
