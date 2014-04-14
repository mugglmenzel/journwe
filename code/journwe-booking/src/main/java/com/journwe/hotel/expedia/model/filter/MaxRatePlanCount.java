package com.journwe.hotel.expedia.model.filter;

import com.journwe.hotel.expedia.model.common.IntegerParameterModel;

/**
 * 
 * Defines the number of room types to return with each property.
 * 
 * Setting a higher value will attempt to return the corresponding number of
 * room types at each property in the response, depending on individual property
 * availabilities.
 * 
 * Defaults to 1, where the only the first room type at each property is
 * returned. Under Expedia user testing, this value has been proven to provide
 * the best conversion rates and is recommended to be left as-is, saving
 * additional rooms for display during the room selection stage.
 * 
 */
public class MaxRatePlanCount extends IntegerParameterModel {

	public MaxRatePlanCount(Integer value) {
		super(value);
	}

}
