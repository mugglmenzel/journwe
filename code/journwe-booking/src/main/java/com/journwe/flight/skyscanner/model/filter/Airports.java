package com.journwe.flight.skyscanner.model.filter;

import java.util.HashSet;

import com.journwe.flight.skyscanner.model.common.AbstractParameterModel;

public abstract class Airports extends AbstractParameterModel {
	
	private HashSet<String> airportCodes = new HashSet<String>();

	public boolean add(String airportCode) {
		return airportCodes.add(airportCode);
	}
	
	public Airports withAirportCode(final String airportCode) {
		add(airportCode);
		return this;
	}

	public String getAirportCodes() {
		StringBuffer toReturn = new StringBuffer();
		int i=0;
		for(String code : airportCodes) {
			toReturn.append(code);
			if(++i < airportCodes.size())
				toReturn.append(";");
		}
		return toReturn.toString();
	}

}
