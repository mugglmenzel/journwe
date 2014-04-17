package com.journwe.flight.skyscanner.query;

import com.journwe.flight.skyscanner.model.location.DestinationPlace;
import com.journwe.flight.skyscanner.model.location.OriginPlace;
import com.journwe.flight.skyscanner.model.reference.Countries;
import com.journwe.flight.skyscanner.model.reference.Country;
import com.journwe.flight.skyscanner.model.reference.Currencies;
import com.journwe.flight.skyscanner.model.reference.Currency;
import com.journwe.flight.skyscanner.model.reference.Locale;
import com.journwe.flight.skyscanner.model.reference.Locales;
import com.journwe.flight.skyscanner.model.time.InboundPartialDate;
import com.journwe.flight.skyscanner.model.time.OutboundPartialDate;

public abstract class Query {
	
	private QueryType queryType;
	private Country country;
	private Locale locale;
	private Currency currency;
    public final static Country DEFAULT_COUNTRY = Countries.get("United Kingdom");
    public final static Locale DEFAULT_LOCALE = Locales.get("en-GB");
    public final static Currency DEFAULT_CURRENCY = Currencies.get("GBP");
    
	private OriginPlace originPlace;
	private DestinationPlace destinationPlace;
	private OutboundPartialDate outboundPartialDate;
	private InboundPartialDate inboundPartialDate;
    
	public Query() {
		setCountry(DEFAULT_COUNTRY);
		setLocale(DEFAULT_LOCALE);
		setCurrency(DEFAULT_CURRENCY);
	}

	public QueryType getQuerytType() {
		return queryType;
	}

	public void setQueryType(
			QueryType browserCacheQuerytType) {
		this.queryType = browserCacheQuerytType;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
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
