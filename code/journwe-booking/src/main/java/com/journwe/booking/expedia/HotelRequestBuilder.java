package com.journwe.booking.expedia;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
