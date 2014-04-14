package com.journwe.hotel.expedia.model.filter;

import com.journwe.hotel.expedia.model.common.BooleanParameterModel;

/**
 * 
 * When sent as false, this parameter will exclude hotels outside of the area
 * defined in your search parameters. Use if you want to prevent hotels from
 * other nearby cities or outlying areas from appearing in your results.
 * 
 */
public class IncludeSurrounding extends BooleanParameterModel {

	public IncludeSurrounding(Boolean value) {
		super(value);
	}

}
