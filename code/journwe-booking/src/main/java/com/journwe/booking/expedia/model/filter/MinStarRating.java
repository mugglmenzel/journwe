package com.journwe.booking.expedia.model.filter;

import com.journwe.booking.expedia.model.common.FloatParameterModel;

/**
 * 
 * Filters results by a minimum star rating. Valid values are 1.0 - 5.0 in
 * increments of 0.5.
 * 
 */
public class MinStarRating extends FloatParameterModel {

	public MinStarRating(Float value) {
		super(value);
	}

}
