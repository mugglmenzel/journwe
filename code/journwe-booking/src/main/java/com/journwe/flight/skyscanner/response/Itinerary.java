package com.journwe.flight.skyscanner.response;

import java.util.List;

public class Itinerary {
	
	private String outboundLegId;
	private String inboundLegId;
	List<PricingOption> pricingOptions;
	
	public Itinerary(String outboundLegId, String inboundLegId,
			List<PricingOption> pricingOptions) {
		super();
		this.outboundLegId = outboundLegId;
		this.inboundLegId = inboundLegId;
		this.pricingOptions = pricingOptions;
	}

	public Itinerary() {
		super();
	}

	public String getOutboundLegId() {
		return outboundLegId;
	}

	public void setOutboundLegId(String outboundLegId) {
		this.outboundLegId = outboundLegId;
	}

	public String getInboundLegId() {
		return inboundLegId;
	}

	public void setInboundLegId(String inboundLegId) {
		this.inboundLegId = inboundLegId;
	}

	public List<PricingOption> getPricingOptions() {
		return pricingOptions;
	}

	public void setPricingOptions(List<PricingOption> pricingOptions) {
		this.pricingOptions = pricingOptions;
	}

}
