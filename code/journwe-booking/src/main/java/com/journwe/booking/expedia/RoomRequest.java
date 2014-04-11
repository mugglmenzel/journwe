package com.journwe.booking.expedia;

public class RoomRequest {
	
	private Integer numberOfAdults = 1;

	public Integer getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(Integer numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}
	
	// Builder pattern for convenience BEGIN
	public RoomRequest withNumberOfAdults(final Integer numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
		return this;
	}
	// Builder pattern for convenience END

}
