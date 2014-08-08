package com.journwe.hotel.expedia.model.filter;

import com.journwe.hotel.expedia.model.common.FloatParameterModel;

/**
 * 
 * Filters results by properties with rates equal to or less than the provided
 * value. Searches against the averageRate response value (average of the
 * individual nightly rates during the dates of stay). Valid for availability
 * searches only.
 * 
 */
public class MaxRate extends FloatParameterModel {

	public MaxRate(Float value) {
		super(value);
	}

}