package com.journwe.booking.expedia;

public enum HotelRequestType {
	
	LIST("list");
	
	private String requestValue;
	
	private HotelRequestType(String requestType) {
		this.requestValue = requestType;
	}
	
	public String getRequestType() {
		return requestValue;
	}

}
