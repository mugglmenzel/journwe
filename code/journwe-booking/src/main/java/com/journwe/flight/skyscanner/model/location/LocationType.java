package com.journwe.flight.skyscanner.model.location;

public enum LocationType {
	
	ANYWHERE("anywhere");
	
	private String locationType;
	
	private LocationType(String locationType) {
		this.locationType = locationType;
	}
	
	public String value() {
		return locationType;
	}

}
