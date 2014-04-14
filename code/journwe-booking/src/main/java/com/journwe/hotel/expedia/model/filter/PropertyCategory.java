package com.journwe.hotel.expedia.model.filter;

import com.journwe.hotel.expedia.model.common.StringParameterModel;

/**
 * Filters results by property category. Send either a single value or a list of
 * values to return a combination of property categories.
 * 
 * Values: 1: hotel 2: suite 3: resort 4: vacation rental/condo 5: bed &
 * breakfast 6: all-inclusive
 * 
 */
public class PropertyCategory extends StringParameterModel {
	
	public static final String HOTEL = "1";
	public static final String SUITE = "2";
	public static final String RESORT = "3";
	public static final String VACATION_RENTAL = "4";
	public static final String BED_AND_BREAKFAST = "5";
	public static final String ALL_INCLUSIVE = "6";
	
	public PropertyCategory(String value) {
		super(value);
	}
	
	public PropertyCategory(String... values) {
		super("");
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<values.length; i++) {
			sb.append(value);
			if(i<values.length) {
				sb.append(",");
			}
		}
		setValue(sb.toString());
	}

}
