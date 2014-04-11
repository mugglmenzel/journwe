package com.journwe.booking.expedia;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class HotelRequest {
	// General stuff
	private HotelRequestType hotelRequestType;
	private String locale;
	private String currency;
    public final static String DEFAULT_LOCALE = "en_US";
    public final static String DEFAULT_CURRENCY = "USD";
	
	// Destination
	private String city;
	private String stateProvinceCode;
	private String countryCode;
	// Time
	private Date arrivalDate;
	private Date departureDate;
	// Rooms
	private ArrayList<RoomRequest> roomRequests = new ArrayList<RoomRequest>();
	
	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	
	public HotelRequest(final HotelRequestType hotelRequestType) {
		this.hotelRequestType = hotelRequestType;
		this.locale = DEFAULT_LOCALE;
		this.currency = DEFAULT_CURRENCY;
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
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStateProvinceCode() {
		return stateProvinceCode;
	}
	public void setStateProvinceCode(String stateProvinceCode) {
		this.stateProvinceCode = stateProvinceCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public Date getArrivalDate() {
		return arrivalDate;
	}
	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	public Date getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}
	public ArrayList<RoomRequest> getRoomRequests() {
		return roomRequests;
	}
	public void setRoomRequests(ArrayList<RoomRequest> roomRequests) {
		this.roomRequests = roomRequests;
	}
	
	// Builder pattern for convenience BEGIN
	public HotelRequest withCity(final String city) {
		this.city = city;
		return this;
	}
	public HotelRequest withStateProvinceCode(final String stateProvinceCode) {
		this.stateProvinceCode = stateProvinceCode;
		return this;
	}
	public HotelRequest withCountryCode(final String countryCode) {
		this.countryCode = countryCode;
		return this;
	}
	public HotelRequest withArrivalDate(final Date arrivalDate) {
		this.arrivalDate = arrivalDate;
		return this;
	}	
	public HotelRequest withDepartureDate(final Date departureDate) {
		this.departureDate = departureDate;
		return this;
	}	
	public HotelRequest withRoomRequests(RoomRequest... req) {
		this.roomRequests = new ArrayList<RoomRequest>(Arrays.asList(req));
		return this;
	}
	// Builder pattern for convenience END
	
	public String generateRequestParameters() {
		StringBuffer toReturn = new StringBuffer();
		if(city!=null) {
		toReturn.append("&city=");
		toReturn.append(city);
		}
		if(stateProvinceCode!=null) {
		toReturn.append("&stateProvinceCode=");
		toReturn.append(stateProvinceCode);
		}
		if(countryCode!=null) {
		toReturn.append("&countryCode=");
		toReturn.append(countryCode);
		}
		if(arrivalDate!=null) {
		toReturn.append("&arrivalDate=");
		toReturn.append(DATE_FORMAT.format(arrivalDate));
		}
		if(arrivalDate!=null) {
		toReturn.append("&departureDate=");
		toReturn.append(DATE_FORMAT.format(departureDate));
		}
		for(int i=1; i<=roomRequests.size(); i++) {
			RoomRequest rr = roomRequests.get(i-1);
			toReturn.append("&room");
			toReturn.append(i);
			toReturn.append("=");
			toReturn.append(rr.getNumberOfAdults());
		}
		return toReturn.toString();
	}
	
}
