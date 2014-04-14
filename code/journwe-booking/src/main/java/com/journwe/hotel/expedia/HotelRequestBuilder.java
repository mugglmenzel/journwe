package com.journwe.hotel.expedia;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.journwe.hotel.expedia.model.destination.City;
import com.journwe.hotel.expedia.model.destination.CountryCode;
import com.journwe.hotel.expedia.model.destination.DestinationString;
import com.journwe.hotel.expedia.model.destination.Latitude;
import com.journwe.hotel.expedia.model.destination.Longitude;
import com.journwe.hotel.expedia.model.destination.SearchRadius;
import com.journwe.hotel.expedia.model.destination.SearchRadiusUnit;
import com.journwe.hotel.expedia.model.destination.StateProvinceCode;

public class HotelRequestBuilder<T extends HotelRequest> {
	
	private Class<T> clazz;
	private T hotelRequest = null;
	
	public HotelRequestBuilder(Class<T> clazz) {
		this.clazz = clazz;
		try {
			hotelRequest = clazz.newInstance();			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Default search query #1 by city
	 * See http://developer.ean.com/docs/hotel-list/
	 * 
	 * @param city
	 * @param stateProvinceCode Only required for US, CA, AU. Set to <code>null</code> for other countries.
	 * @param countryCode
	 * @return
	 */
	public HotelRequestBuilder<T> with(City city, StateProvinceCode stateProvinceCode, CountryCode countryCode) {
		if(hotelRequest instanceof HotelListRequest) {
			HotelListRequest hlr = (HotelListRequest)hotelRequest;
			hlr.setCity(city);
			if(countryCode.getValue().equals("US") || countryCode.getValue().equals("CA") || countryCode.getValue().equals("AU"))
				hlr.setStateProvinceCode(stateProvinceCode);
			else
				hlr.setStateProvinceCode(null);
			hlr.setCountryCode(countryCode);
		}
		return this;
	}
	
	/**
	 * Default search query #1 for non-US, non-Canada, non-Australia countries.
	 * See http://developer.ean.com/docs/hotel-list/
	 * 
	 * @param city
	 * @param countryCode
	 * @return
	 */
	public HotelRequestBuilder<T> with(City city, CountryCode countryCode) {
		return with(city, null, countryCode);
	}

	/**
	 * Default search query #2 by free-text destination search
	 * See http://developer.ean.com/docs/hotel-list/
	 * 
	 * @param destinationString
	 * @param countryCode
	 * @return
	 */
	public HotelRequestBuilder<T> with(DestinationString destinationString) {
		if(hotelRequest instanceof HotelListRequest) {
			HotelListRequest hlr = (HotelListRequest)hotelRequest;
			hlr.setDestinationString(destinationString);
		}
		return this;
	}
	
	/**
	 * Default search query #5 by geo locaction
	 * See http://developer.ean.com/docs/hotel-list/
	 * 
	 */
	public HotelRequestBuilder<T> with(Latitude latitude, Longitude longitude, SearchRadius searchRadius, SearchRadiusUnit searchRadiusUnit) {
		if(hotelRequest instanceof HotelListRequest) {
			HotelListRequest hlr = (HotelListRequest)hotelRequest;
			hlr.setLatitude(latitude);
			hlr.setLongitude(longitude);
			hlr.setSearchRadius(searchRadius);
			hlr.setSearchRadiusUnit(searchRadiusUnit);
		}
		return this;
	}
	
	public HotelRequestBuilder<T> with(Object obj) {
		Method[] allMethods = clazz.getDeclaredMethods();
	    for (Method m : allMethods) {
	    	String methodName = m.getName();
	    	if(methodName.startsWith("set")) {
	    		if(methodName.substring(3).equalsIgnoreCase(obj.getClass().getSimpleName())) {
	    			try {
						m.invoke(hotelRequest, obj);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
	    		}
	    	}
	    }
		return this;
	}
	
	public T build() {
		return hotelRequest;
	}

}
