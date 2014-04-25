package com.journwe.flight.skyscanner.model.filter;

public class OriginAirports extends Airports {
	
	@Override
	public String getValue() {
		return getAirportCodes();
	}

	public String getQueryString() {
		return "&originairports="+this.getAirportCodes();
	}

}
