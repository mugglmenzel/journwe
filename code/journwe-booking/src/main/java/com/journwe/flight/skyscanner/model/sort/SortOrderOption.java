package com.journwe.flight.skyscanner.model.sort;

public enum SortOrderOption {
	
	ASC("asc"), DESC("desc");
	
	private String order;
	
	private SortOrderOption(String order) {
		this.order = order;
	}
	
	public String value() {
		return order;
	}

}
