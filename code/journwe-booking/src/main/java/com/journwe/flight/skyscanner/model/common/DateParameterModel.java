package com.journwe.flight.skyscanner.model.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParameterModel extends AbstractParameterModel {
	
	private Date value;
	
	public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public DateParameterModel(Date value) {
		super();
		this.value = value;
	}

	@Override
	public String getValue() {
		return DATE_FORMAT.format(value);
	}

	public void setValue(Date value) {
		this.value = value;
	}
}
