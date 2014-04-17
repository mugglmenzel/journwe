package com.journwe.flight.skyscanner.query.livepricing;

public class LivePricingPollQuery extends LivePricingQuery {

	private String sessionUrl;

	public LivePricingPollQuery(final String sessionUrl) {
		this.sessionUrl = sessionUrl;
	}

	public String getSessionUrl() {
		return sessionUrl;
	}

	public void setSessionUrl(String sessionUrl) {
		this.sessionUrl = sessionUrl;
	}

}
