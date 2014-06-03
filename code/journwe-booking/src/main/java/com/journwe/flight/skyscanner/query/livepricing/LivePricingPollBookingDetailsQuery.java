package com.journwe.flight.skyscanner.query.livepricing;

public class LivePricingPollBookingDetailsQuery extends BookingDetailsQuery {
	
	private String sessionUrl;
	private String bookingPollUrl;
	
	public LivePricingPollBookingDetailsQuery(String sessionUrl, String bookingPollUrl) {
		super();
		this.sessionUrl = sessionUrl;
		this.bookingPollUrl = bookingPollUrl;
	}

	public String getSessionUrl() {
		return sessionUrl;
	}

	public void setSessionUrl(String sessionUrl) {
		this.sessionUrl = sessionUrl;
	}

	public String getBookingPollUrl() {
		return bookingPollUrl;
	}

	public void setBookingPollUrl(String bookingPollUrl) {
		this.bookingPollUrl = bookingPollUrl;
	}

}
