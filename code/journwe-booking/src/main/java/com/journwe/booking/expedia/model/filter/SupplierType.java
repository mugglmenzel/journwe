package com.journwe.booking.expedia.model.filter;

import com.journwe.booking.expedia.model.common.StringParameterModel;

/**
 * 
 * Filters results by supplier type. This parameter is typically used to
 * restrict initial searches to Expedia Collect inventory only. If no
 * availability is found, the parameter is omitted to allow Hotel Collect to
 * return in a secondary search.
 * 
 * Values: E: Expedia Collect V: Venere (Hotel Collect ) E|V: Expedia Collect
 * and Venere S: Sabre (Hotel Collect ) W: Worldspan (Hotel Collect )
 * 
 * Only values of E or E|V are recommended.Other values will exclude Expedia
 * Collect inventory and return only Hotel Collect properties that inherently
 * lack reliable availability and commission guarantees.
 * 
 * Avoid using this parameter when requesting specific hotelIds from our
 * database files. Supplier errors are likely to result.
 */
public class SupplierType extends StringParameterModel {

	public SupplierType(String value) {
		super(value);
	}

}
