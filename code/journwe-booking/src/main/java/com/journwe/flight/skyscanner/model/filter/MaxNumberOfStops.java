package com.journwe.flight.skyscanner.model.filter;

import com.journwe.flight.skyscanner.model.common.IntegerParameterModel;

public class MaxNumberOfStops extends IntegerParameterModel {

	public MaxNumberOfStops(Integer value) {
		super(value);
		if(value < 0)
			setValue(0);
		if(value > 3)
			setValue(3);
	}

	public String getQueryString() {
		return "&stops="+value;
	}
}
