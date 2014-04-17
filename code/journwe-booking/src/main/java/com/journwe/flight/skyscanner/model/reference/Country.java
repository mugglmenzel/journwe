package com.journwe.flight.skyscanner.model.reference;

public class Country {

	private String name;
	private String code;
	
	public Country(String name, String code) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
