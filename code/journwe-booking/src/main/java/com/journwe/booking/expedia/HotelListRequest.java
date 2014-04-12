package com.journwe.booking.expedia;

import com.journwe.booking.expedia.model.ArrivalDate;
import com.journwe.booking.expedia.model.City;
import com.journwe.booking.expedia.model.CountryCode;
import com.journwe.booking.expedia.model.DepartureDate;
import com.journwe.booking.expedia.model.Rooms;
import com.journwe.booking.expedia.model.StateProvinceCode;

public class HotelListRequest extends HotelRequest {
	
	// Destination
	@HotelRequestParameter(name = "city")
	public City city;
	@HotelRequestParameter(name = "stateProvinceCode")
	public StateProvinceCode stateProvinceCode;
	@HotelRequestParameter(name = "countryCode")
	public CountryCode countryCode;
	// Time
	@HotelRequestParameter(name = "arrivalDate")
	public ArrivalDate arrivalDate;
	@HotelRequestParameter(name = "departureDate")
	public DepartureDate departureDate;
	// Rooms
	@HotelRequestParameter(name = "rooms")
	public Rooms rooms;

	public HotelListRequest() {
		super();
		setHotelRequestType(HotelRequestType.LIST);
	}
	
//	public String generateRequestParameters() {
//		StringBuffer toReturn = new StringBuffer();
//		
//		
//		if(city!=null) {
//		toReturn.append("&city=");
//		toReturn.append(city);
//		}
//		if(stateProvinceCode!=null) {
//		toReturn.append("&stateProvinceCode=");
//		toReturn.append(stateProvinceCode);
//		}
//		if(countryCode!=null) {
//		toReturn.append("&countryCode=");
//		toReturn.append(countryCode);
//		}
//		if(arrivalDate!=null) {
//		toReturn.append("&arrivalDate=");
//		toReturn.append(DATE_FORMAT.format(arrivalDate));
//		}
//		if(arrivalDate!=null) {
//		toReturn.append("&departureDate=");
//		toReturn.append(DATE_FORMAT.format(departureDate));
//		}
//		for(int i=1; i<=roomRequests.size(); i++) {
//			RoomRequest rr = roomRequests.get(i-1);
//			toReturn.append("&room");
//			toReturn.append(i);
//			toReturn.append("=");
//			toReturn.append(rr.getNumberOfAdults());
//		}
//		return toReturn.toString();
//	}

	public City getCity() {
		return city;
	}



	public void setCity(City city) {
		this.city = city;
	}



	public StateProvinceCode getStateProvinceCode() {
		return stateProvinceCode;
	}



	public void setStateProvinceCode(StateProvinceCode stateProvinceCode) {
		this.stateProvinceCode = stateProvinceCode;
	}



	public CountryCode getCountryCode() {
		return countryCode;
	}



	public void setCountryCode(CountryCode countryCode) {
		this.countryCode = countryCode;
	}



	public ArrivalDate getArrivalDate() {
		return arrivalDate;
	}



	public void setArrivalDate(ArrivalDate arrivalDate) {
		this.arrivalDate = arrivalDate;
	}



	public DepartureDate getDepartureDate() {
		return departureDate;
	}



	public void setDepartureDate(DepartureDate departureDate) {
		this.departureDate = departureDate;
	}



	public Rooms getRooms() {
		return rooms;
	}



	public void setRooms(Rooms rooms) {
		this.rooms = rooms;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
