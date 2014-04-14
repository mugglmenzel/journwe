package com.journwe.hotel.expedia.model.common;

public class BooleanParameterModel extends AbstractParameterModel {

	public Boolean value;

	public BooleanParameterModel(Boolean value) {
		super();
		this.value = value;
	}

	@Override
	public String getValue() {
		return value.toString();
	}

	public void setValue(Boolean value) {
		this.value = value;
	}
}
