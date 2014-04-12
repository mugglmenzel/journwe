package com.journwe.booking.expedia;

import java.lang.reflect.Field;

import com.journwe.booking.expedia.model.common.AbstractParameterModel;

public class RequestGenerator {
	
	public static final String BASE_URL = "http://api.ean.com/ean-services/rs/hotel/v3/";
	public static final String MINOR_REV = "99";
	
	/**
	 * Generate a URL for REST request.
	 * 
	 * @param hotelRequest
	 * @return
	 */
	public static String generateUrl(final String apiKey, final String cid, final HotelRequest hotelRequest) {
		StringBuffer toReturn = new StringBuffer();
		toReturn.append(BASE_URL);
		toReturn.append(hotelRequest.getHotelRequestType().getRequestType());
		toReturn.append("?minorRev=");				
		toReturn.append(MINOR_REV);		
		toReturn.append("&apiKey=");
		toReturn.append(apiKey);
		toReturn.append("&cid=");
		toReturn.append(cid);
		toReturn.append(generateRequestUrlParameters(hotelRequest));
		return toReturn.toString();
	}
	
	/**
	 * The method generates URL parameters in the form <code>&name=value</code> from a HotelRequest java object.
	 * 
	 * @param hotelRequest
	 * @return the URL parameters
	 */
	private static String generateRequestUrlParameters(HotelRequest hotelRequest) {
		StringBuffer toReturn = new StringBuffer();
		System.out.println(hotelRequest.getClass());
		Field[] fields = hotelRequest.getClass().getFields();
		for (Field field : fields) {
			// Retrieve fields that are annotated with HotelRequestParameter.
            HotelRequestParameter annos = field.getAnnotation(HotelRequestParameter.class);
            if (annos != null) {
                try {
                	field.setAccessible(true);
                	Object value = field.get(hotelRequest);
                	// Proceed, if value is set.
                	if(value != null) {
                		// The param name
                		String name = annos.name();
                		if(name == null)
                			name = field.getName();
                		// rooms must be treated specially
                		if(name.equalsIgnoreCase("rooms")) {
                			toReturn.append(((AbstractParameterModel)value).getValue());
                		} else {
                		// The param value
                		String stringyfiedValue = ((AbstractParameterModel)value).getValue();              		
                		// Append &name=value
                		toReturn.append("&");
                		toReturn.append(name);
                		toReturn.append("=");
                		toReturn.append(stringyfiedValue);
                		}
                	}
                    field.setAccessible(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
		return toReturn.toString();
	}    

}
