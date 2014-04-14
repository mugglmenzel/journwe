package com.journwe.flight.skyscanner.query;

import com.journwe.flight.skyscanner.model.location.DestinationPlace;
import com.journwe.flight.skyscanner.model.location.OriginPlace;
import com.journwe.flight.skyscanner.model.time.InboundPartialDate;
import com.journwe.flight.skyscanner.model.time.OutboundPartialDate;

public abstract class BrowseCacheQuery {
	
	private BrowseCacheQueryType browserCacheQuerytType;
	private String country;
	private String locale;
	private String currency;
    public final static String DEFAULT_COUNTRY = "GB";
    public final static String DEFAULT_LOCALE = "en-GB";
    public final static String DEFAULT_CURRENCY = "GBP";
    
	private OriginPlace originPlace;
	private DestinationPlace destinationPlace;
	private OutboundPartialDate outboundPartialDate;
	private InboundPartialDate inboundPartialDate;
	
	public BrowseCacheQuery() {
		setCountry(DEFAULT_COUNTRY);
		setLocale(DEFAULT_LOCALE);
		setCurrency(DEFAULT_CURRENCY);
	}

	public BrowseCacheQueryType getBrowserCacheQuerytType() {
		return browserCacheQuerytType;
	}

	public void setBrowserCacheQueryType(
			BrowseCacheQueryType browserCacheQuerytType) {
		this.browserCacheQuerytType = browserCacheQuerytType;
	}	
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}	
	
	public OriginPlace getOriginPlace() {
		return originPlace;
	}

	public void setOriginPlace(OriginPlace originPlace) {
		this.originPlace = originPlace;
	}

	public DestinationPlace getDestinationPlace() {
		return destinationPlace;
	}

	public void setDestinationPlace(DestinationPlace destinationPlace) {
		this.destinationPlace = destinationPlace;
	}

	public OutboundPartialDate getOutboundPartialDate() {
		return outboundPartialDate;
	}

	public void setOutboundPartialDate(OutboundPartialDate outboundPartialDate) {
		this.outboundPartialDate = outboundPartialDate;
	}

	public InboundPartialDate getInboundPartialDate() {
		return inboundPartialDate;
	}

	public void setInboundPartialDate(InboundPartialDate inboundPartialDate) {
		this.inboundPartialDate = inboundPartialDate;
	}

}
