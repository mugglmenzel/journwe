package com.journwe.booking.expedia.model.common;

public class StringParameterModel extends AbstractParameterModel {

	public String value;

	public StringParameterModel(String value) {
		super();
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
