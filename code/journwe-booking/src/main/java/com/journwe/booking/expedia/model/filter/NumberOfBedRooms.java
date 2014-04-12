package com.journwe.booking.expedia.model.filter;

import com.journwe.booking.expedia.model.common.IntegerParameterModel;

/**
 * 
 * This parameter is valid for condos/vacation rentals only. Specifies the
 * number of bedrooms requested - 4 maximum.
 * 
 */
public class NumberOfBedRooms extends IntegerParameterModel {

	public NumberOfBedRooms(Integer value) {
		super(value);
	}

}
