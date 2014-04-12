package com.journwe.booking.expedia;


public abstract class HotelRequest {

	private HotelRequestType hotelRequestType;
	private String locale;
	private String currency;
    public final static String DEFAULT_LOCALE = "en_US";
    public final static String DEFAULT_CURRENCY = "USD";	
	
	public HotelRequest() {
		setLocale(DEFAULT_LOCALE);
		setCurrency(DEFAULT_CURRENCY);
	}	
	
	public HotelRequestType getHotelRequestType() {
		return hotelRequestType;
	}
	public void setHotelRequestType(HotelRequestType hotelRequestType) {
		this.hotelRequestType = hotelRequestType;
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
	
}
