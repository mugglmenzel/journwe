package com.journwe.flight.skyscanner.model.sort;

import com.journwe.flight.skyscanner.model.common.StringParameterModel;

public class SortType extends StringParameterModel  {

	public SortType(String value) {
		super(value);
	}
	
	public SortType(SortTypeOption sortTypeOption) {
		super(sortTypeOption.value());
	}

	public String getQueryString() {
		return "&sorttype="+this.value;
	}
	
}
