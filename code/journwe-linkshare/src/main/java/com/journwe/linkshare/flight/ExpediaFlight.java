package com.journwe.linkshare.flight;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class ExpediaFlight {
	
	HashMap<String,String> params = new HashMap<String,String>();
	
	public ExpediaFlight() {
		params.put("inpFlightRouteType", "2");
		params.put("inpDepartureLocations", "");
		params.put("inpArrivalLocations", "");
		params.put("inpDepartureDates", "");
		params.put("inpArrivalDates", "");
		params.put("inpAdultCounts", "1");
		params.put("inpChildCounts", "0");
		params.put("inpChildAges", "-1");
		params.put("inpFlightAirlinePreference", "");
		params.put("inpFlightClass", "3");
	}

	public boolean containsKey(String key) {
		return params.containsKey(key);
	}

	public String get(String key) {
		return params.get(key);
	}

	public String put(String key, String value) {
		return params.put(key, value);
	}

	public Set<Entry<String, String>> entrySet() {
		return params.entrySet();
	}
	
}
