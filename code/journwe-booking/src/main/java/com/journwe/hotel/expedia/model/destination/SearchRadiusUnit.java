package com.journwe.hotel.expedia.model.destination;

import com.journwe.hotel.expedia.model.common.StringParameterModel;

/**
 * 
 * Sets the unit of distance for the search radius. Send MI or KM. Defaults to
 * MI if empty or not included.
 * 
 */
public class SearchRadiusUnit extends StringParameterModel {
	
	public static final String MILES = "MI";
	public static final String KILOMETERS = "KM";
	
	public SearchRadiusUnit(String value) {
		super(value);
	}
	
}
