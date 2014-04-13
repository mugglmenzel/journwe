package com.journwe.booking.expedia;

import java.lang.reflect.Field;

import com.journwe.booking.expedia.auth.SignatureGenerator;
import com.journwe.booking.expedia.model.common.AbstractParameterModel;

public class RequestGenerator {
	
	//public static final String BASE_URL = "http://api.ean.com/ean-services/rs/hotel/v3/";
	public static final String BASE_URL = "https://api.eancdn.com/ean-services/rs/hotel/v3/"; // SANDBOX
	public static final String MINOR_REV = "26";
	public static final Integer NUMBER_OF_RESULTS = 20; // max 200
	
	/**
	 * Generate a URL for REST request.
	 * 
	 * @param hotelRequest
	 * @return
	 */
	public static String generateUrl(final String apiKey, final String cid, final String secret, final HotelRequest hotelRequest) {
		StringBuffer toReturn = new StringBuffer();
		toReturn.append(BASE_URL);
		toReturn.append(hotelRequest.getHotelRequestType().getRequestType());
		toReturn.append("?minorRev=");				
		toReturn.append(MINOR_REV);		
		toReturn.append("&apiKey=");
		toReturn.append(apiKey);
		toReturn.append("&cid=");
		toReturn.append(cid);
		toReturn.append("&sig=");
		String signature = SignatureGenerator.generateMD5Signature(apiKey, secret);
		toReturn.append(signature);
		toReturn.append("&customerIpAddress=95.114.46.27");
		//toReturn.append("&customerUserAgent='Mozilla/4.0'");
		toReturn.append("&customerSessionId=0ABAAA8E-D630-3E91-4552-ACC125095B40");
		toReturn.append(generateRequestUrlParameters(hotelRequest));
		if(NUMBER_OF_RESULTS!=20 && NUMBER_OF_RESULTS>=1 && NUMBER_OF_RESULTS<=200) {
				toReturn.append("&numberOfResults=");
				toReturn.append(NUMBER_OF_RESULTS);	
		}
		return toReturn.toString();
	}
	//toReturn.append("http://api.ean.com/ean-services/rs/hotel/v3/list?cid=55505&minorRev=26&apiKey=cbrzfta369qwyrm9t5b8y8kf&locale=en_US&currencyCode=USD&customerIpAddress=95.114.46.27&customerUserAgent=Mozilla/5.0+(Macintosh;+Intel+Mac+OS+X+10.9;+rv:28.0)+Gecko/20100101+Firefox/28.0&customerSessionId=&xml=<HotelListRequest><arrivalDate>05/04/2014</arrivalDate><departureDate>05/06/2014</departureDate><RoomGroup><Room><numberOfAdults>0</numberOfAdults></Room></RoomGroup><city>Amsterdam</city><countryCode>NL</countryCode></HotelListRequest>");
//	toReturn.append("&customerIpAddress=95.114.46.27&supplierCacheTolerance=MED&supplierCacheTolerance=MED_ENHANCED&customerSessionId=1726373627263"); 
	
	/**
	 * The method generates URL parameters in the form <code>&name=value</code> from a HotelRequest java object.
	 * 
	 * @param hotelRequest
	 * @return the URL parameters
	 */
	protected static String generateRequestUrlParameters(HotelRequest hotelRequest) {
		StringBuffer toReturn = new StringBuffer();
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
