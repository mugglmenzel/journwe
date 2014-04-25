package com.journwe.flight.skyscanner.model.sort;

public enum SortTypeOption {
	
	CARRIER("carrier"), SURATION("duration"), OUTBOUND_ARRIVE_TIME("outboundarrivetime"),
	OUTRBOUND_DEPART_TIME("outbounddeparttime"), INBOUND_ARRIVE_TIME("inboundarrivetime"), 
	INBOUND_DEPART_TIME("inbounddeparttime"), PRICE("price");
	
	private String sortType;
	
	private SortTypeOption(String sortType) {
		this.sortType = sortType;
	}
	
	public String value() {
		return sortType;
	}

}
