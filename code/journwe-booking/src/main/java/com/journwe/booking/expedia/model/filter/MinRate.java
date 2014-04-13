package com.journwe.booking.expedia.model.filter;

import com.journwe.booking.expedia.model.common.FloatParameterModel;

/**
 * 
 * Filters results by properties with rates equal to or greater than the
 * provided value. Searches against the averageRate response value (average of
 * the individual nightly rates during the dates of stay). Valid for
 * availability searches only.
 * 
 */
public class MinRate extends FloatParameterModel {

	public MinRate(Float value) {
		super(value);
	}

}
