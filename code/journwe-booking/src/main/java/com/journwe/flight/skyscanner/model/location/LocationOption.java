package com.journwe.flight.skyscanner.model.location;

public enum LocationOption {
	
	ANYWHERE("anywhere");
	
	private String locationType;
	
	private LocationOption(String locationType) {
		this.locationType = locationType;
	}
	
	public String value() {
		return locationType;
	}

}
