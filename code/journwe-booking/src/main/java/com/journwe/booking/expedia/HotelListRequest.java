package com.journwe.booking.expedia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.journwe.booking.expedia.model.destination.Address;
import com.journwe.booking.expedia.model.destination.City;
import com.journwe.booking.expedia.model.destination.CountryCode;
import com.journwe.booking.expedia.model.destination.DestinationString;
import com.journwe.booking.expedia.model.destination.Latitude;
import com.journwe.booking.expedia.model.destination.Longitude;
import com.journwe.booking.expedia.model.destination.PostalCode;
import com.journwe.booking.expedia.model.destination.PropertyName;
import com.journwe.booking.expedia.model.destination.SearchRadius;
import com.journwe.booking.expedia.model.destination.SearchRadiusUnit;
import com.journwe.booking.expedia.model.destination.StateProvinceCode;
import com.journwe.booking.expedia.model.filter.IncludeSurrounding;
import com.journwe.booking.expedia.model.filter.MaxRate;
import com.journwe.booking.expedia.model.filter.MaxRatePlanCount;
import com.journwe.booking.expedia.model.filter.MaxStarRating;
import com.journwe.booking.expedia.model.filter.MinRate;
import com.journwe.booking.expedia.model.filter.MinStarRating;
import com.journwe.booking.expedia.model.filter.NumberOfBedRooms;
import com.journwe.booking.expedia.model.filter.PropertyCategory;
import com.journwe.booking.expedia.model.filter.SupplierType;
import com.journwe.booking.expedia.model.room.Room;
import com.journwe.booking.expedia.model.room.Rooms;
import com.journwe.booking.expedia.model.sort.Sort;
import com.journwe.booking.expedia.model.sort.SortType;
import com.journwe.booking.expedia.model.time.ArrivalDate;
import com.journwe.booking.expedia.model.time.DepartureDate;

public class HotelListRequest extends HotelRequest {

	// Destination search by city
	@HotelRequestParameter(name = "city")
	public City city;
	@HotelRequestParameter(name = "address")
	public Address address;
	@HotelRequestParameter(name = "postalCode")
	public PostalCode postalCode;
	@HotelRequestParameter(name = "propertyName")
	public PropertyName propertyName;
	@HotelRequestParameter(name = "stateProvinceCode")
	public StateProvinceCode stateProvinceCode;
	@HotelRequestParameter(name = "countryCode")
	public CountryCode countryCode;
	// Destination search by free text search
	@HotelRequestParameter(name = "destinationString")
	public DestinationString destinationString;
	// Destination search by geo location
	@HotelRequestParameter(name = "latitude")
	public Latitude latitude;
	@HotelRequestParameter(name = "longitude")
	public Longitude longitude;
	@HotelRequestParameter(name = "searchRadius")
	public SearchRadius searchRadius;
	@HotelRequestParameter(name = "searchRadiusUnit")
	public SearchRadiusUnit searchRadiusUnit;

	// Time
	@HotelRequestParameter(name = "arrivalDate")
	public ArrivalDate arrivalDate;
	@HotelRequestParameter(name = "departureDate")
	public DepartureDate departureDate;
	// Rooms
	@HotelRequestParameter(name = "rooms")
	public Rooms rooms;

	// Filters
	@HotelRequestParameter(name = "includeSurrounding")
	public IncludeSurrounding includeSurrounding;
	@HotelRequestParameter(name = "propertyCategory")
	public PropertyCategory propertyCategory;
	@HotelRequestParameter(name = "maxStarRating")
	public MaxStarRating maxStarRating;
	@HotelRequestParameter(name = "minStarRating")
	public MinStarRating minStarRating;
	@HotelRequestParameter(name = "minRate")
	public MinRate minRate;
	@HotelRequestParameter(name = "maxRate")
	public MaxRate maxRate;
	@HotelRequestParameter(name = "numberOfBedRooms")
	public NumberOfBedRooms numberOfBedRooms;
	@HotelRequestParameter(name = "supplierType")
	public SupplierType supplierType;
	@HotelRequestParameter(name = "maxRatePlanCount")
	public MaxRatePlanCount maxRatePlanCount;

	// Sort options
	@HotelRequestParameter(name = "sort")
	public Sort sort;

	public HotelListRequest() {
		super();
		setHotelRequestType(HotelRequestType.LIST);
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public PostalCode getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(PostalCode postalCode) {
		this.postalCode = postalCode;
	}

	public PropertyName getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(PropertyName propertyName) {
		this.propertyName = propertyName;
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

	public DestinationString getDestinationString() {
		return destinationString;
	}

	public void setDestinationString(DestinationString destinationString) {
		this.destinationString = destinationString;
	}

	public Latitude getLatitude() {
		return latitude;
	}

	public void setLatitude(Latitude latitude) {
		this.latitude = latitude;
	}

	public Longitude getLongitude() {
		return longitude;
	}

	public void setLongitude(Longitude longitude) {
		this.longitude = longitude;
	}

	public SearchRadius getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(SearchRadius searchRadius) {
		this.searchRadius = searchRadius;
	}

	public SearchRadiusUnit getSearchRadiusUnit() {
		return searchRadiusUnit;
	}

	public void setSearchRadiusUnit(SearchRadiusUnit searchRadiusUnit) {
		this.searchRadiusUnit = searchRadiusUnit;
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

	public IncludeSurrounding getIncludeSurrounding() {
		return includeSurrounding;
	}

	public void setIncludeSurrounding(IncludeSurrounding includeSurrounding) {
		this.includeSurrounding = includeSurrounding;
	}

	public PropertyCategory getPropertyCategory() {
		return propertyCategory;
	}

	public void setPropertyCategory(PropertyCategory propertyCategory) {
		this.propertyCategory = propertyCategory;
	}

	public MaxStarRating getMaxStarRating() {
		return maxStarRating;
	}

	public void setMaxStarRating(MaxStarRating maxStarRating) {
		this.maxStarRating = maxStarRating;
	}

	public MinStarRating getMinStarRating() {
		return minStarRating;
	}

	public void setMinStarRating(MinStarRating minStarRating) {
		this.minStarRating = minStarRating;
	}

	public MinRate getMinRate() {
		return minRate;
	}

	public void setMinRate(MinRate minRate) {
		this.minRate = minRate;
	}

	public MaxRate getMaxRate() {
		return maxRate;
	}

	public void setMaxRate(MaxRate maxRate) {
		this.maxRate = maxRate;
	}

	public NumberOfBedRooms getNumberOfBedRooms() {
		return numberOfBedRooms;
	}

	public void setNumberOfBedRooms(NumberOfBedRooms numberOfBedRooms) {
		this.numberOfBedRooms = numberOfBedRooms;
	}

	public SupplierType getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(SupplierType supplierType) {
		this.supplierType = supplierType;
	}

	public MaxRatePlanCount getMaxRatePlanCount() {
		return maxRatePlanCount;
	}

	public void setMaxRatePlanCount(MaxRatePlanCount maxRatePlanCount) {
		this.maxRatePlanCount = maxRatePlanCount;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	/**
	 * For testing purposes only.
	 * 
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date june10 = sdf.parse("10/06/2014");
		Date june15 = sdf.parse("15/06/2014");
		HotelListRequest req = new HotelRequestBuilder<HotelListRequest>(
				HotelListRequest.class).with(new City("Seattle"))
				.with(new CountryCode("US")).with(new StateProvinceCode("WA"))
				.with(new Rooms(new Room(2, 5, 13)))
				.with(new ArrivalDate(june10)).with(new DepartureDate(june15))
				.with(new MinStarRating(3.0F))
				.with(new Sort(SortType.PRICE_AVERAGE)).build();
		String url = RequestGenerator.generateRequestUrlParameters(req);
		System.out.println(url);
	}

}
