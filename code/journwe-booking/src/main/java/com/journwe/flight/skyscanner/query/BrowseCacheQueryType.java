package com.journwe.flight.skyscanner.query;

public enum BrowseCacheQueryType {

	BROWSE_QUOTES("browsequotes/v1.0/"), BROWSE_ROUTES("browseroutes/v1.0/"), BROWSE_DATES(
			"browsedates/v1.0/"), BROWSE_GRID("browsegrid/v1.0/");

	private String requestValue;

	private BrowseCacheQueryType(String browseCacheRequest) {
		this.requestValue = browseCacheRequest;
	}

	public String value() {
		return requestValue;
	}

}
