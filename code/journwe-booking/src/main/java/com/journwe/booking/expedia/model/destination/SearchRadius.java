package com.journwe.booking.expedia.model.destination;

import com.journwe.booking.expedia.model.common.IntegerParameterModel;

/**
 * Defines size of a square area to be searched within - not an actual radius of
 * a circle. The value is still treated as a radius in the sense that it is half
 * the width of the search area. Factor in the added area and maximum distances
 * this creates vs a circular search area when exposing this value directly.
 * 
 * Example
 * Minimum of 1 MI or 2 KM, maximum of 50 MI or 80 KM. Defaults to 20
 * MI.
 * 
 */
public class SearchRadius extends IntegerParameterModel {

	public SearchRadius(Integer value) {
		super(value);
	}

}
