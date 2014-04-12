package com.journwe.booking.expedia.model.common;

public class IntegerParameterModel extends AbstractParameterModel {

	public Integer value;

	public IntegerParameterModel(Integer value) {
		super();
		this.value = value;
	}

	@Override
	public String getValue() {
		return value.toString();
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
