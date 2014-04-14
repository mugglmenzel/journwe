package com.journwe.hotel.expedia.model.common;

public class FloatParameterModel extends AbstractParameterModel {

	public Float value;

	public FloatParameterModel(Float value) {
		super();
		this.value = value;
	}

	@Override
	public String getValue() {
		return value.toString();
	}

	public void setValue(Float value) {
		this.value = value;
	}
}
