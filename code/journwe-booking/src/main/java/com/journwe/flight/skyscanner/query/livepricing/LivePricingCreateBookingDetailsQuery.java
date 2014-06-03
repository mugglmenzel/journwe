package com.journwe.flight.skyscanner.query.livepricing;


public class LivePricingCreateBookingDetailsQuery extends BookingDetailsQuery {

	private String sessionUrl;
	private String outboundlegId;
	private String inboundlegId;

	public LivePricingCreateBookingDetailsQuery(String sessionUrl,
			String outboundlegId, String inboundlegId) {
		super();
		this.sessionUrl = sessionUrl;
		this.outboundlegId = outboundlegId;
		this.inboundlegId = inboundlegId;
	}

	public String getSessionUrl() {
		return sessionUrl;
	}

	public void setSessionUrl(String sessionUrl) {
		this.sessionUrl = sessionUrl;
	}

	public String getOutboundlegId() {
		return outboundlegId;
	}

	public void setOutboundlegId(String outboundlegId) {
		this.outboundlegId = outboundlegId;
	}

	public String getInboundlegId() {
		return inboundlegId;
	}

	public void setInboundlegId(String inboundlegId) {
		this.inboundlegId = inboundlegId;
	}
	
	public String getQueryString() {
		StringBuffer toReturn = new StringBuffer();
		if(outboundlegId!=null) {
			toReturn.append("&outboundlegid=");
			toReturn.append(outboundlegId);
		}
		if(inboundlegId!=null) {
			toReturn.append("&inboundlegid=");
			toReturn.append(inboundlegId);
		}
		return toReturn.toString();
	}
	
}
