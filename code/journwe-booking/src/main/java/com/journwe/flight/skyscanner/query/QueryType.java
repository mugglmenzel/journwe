package com.journwe.flight.skyscanner.query;

public enum QueryType {

	BROWSE_QUOTES("browsequotes/v1.0"), BROWSE_ROUTES("browseroutes/v1.0"), BROWSE_DATES(
			"browsedates/v1.0"), BROWSE_GRID("browsegrid/v1.0"), PRICING("pricing/v1.0");

	private String requestValue;

	private QueryType(String requestValue) {
		this.requestValue = requestValue;
	}

	public String value() {
		return requestValue;
	}

}
