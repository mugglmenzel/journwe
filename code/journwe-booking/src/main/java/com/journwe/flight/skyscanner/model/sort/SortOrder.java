package com.journwe.flight.skyscanner.model.sort;

import com.journwe.flight.skyscanner.model.common.StringParameterModel;

public class SortOrder extends StringParameterModel {

	public SortOrder(String value) {
		super(value);
	}
	
	public SortOrder(SortOrderOption sortOrderOption) {
		super(sortOrderOption.value());
	}
	
	public String getQueryString() {
		return "&sortorder="+this.value;
	}
}
