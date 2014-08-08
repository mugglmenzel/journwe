package com.journwe.hotel.expedia.model.sort;

import com.journwe.hotel.expedia.model.common.AbstractParameterModel;

public class Sort extends AbstractParameterModel {

	private static SortType value;

	public Sort(SortType sortType) {
		value = sortType;
	}

	@Override
	public String getValue() {
		return value.name();
	}

	public static void setValue(SortType value) {
		Sort.value = value;
	}

}