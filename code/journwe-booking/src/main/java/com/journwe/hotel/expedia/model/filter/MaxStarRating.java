package com.journwe.hotel.expedia.model.filter;

import com.journwe.hotel.expedia.model.common.FloatParameterModel;

/**
 * 
 * Filters results by a maximum star rating. Valid values are 1.0 - 5.0 in
 * increments of 0.5.
 * 
 */
public class MaxStarRating extends FloatParameterModel {

	public MaxStarRating(Float value) {
		super(value);
	}

}
