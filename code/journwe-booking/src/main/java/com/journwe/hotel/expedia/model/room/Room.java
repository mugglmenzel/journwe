package com.journwe.hotel.expedia.model.room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.journwe.hotel.expedia.model.common.AbstractParameterModel;

public class Room extends AbstractParameterModel {
	private Integer numberOfAdults = 1;
	private List<Integer> agesOfChildren = new ArrayList<Integer>();

	public Room() {

	}

	public Room(Integer numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public Room(Integer numberOfAdults, Integer... agesOfChildren) {
		this(numberOfAdults);
		this.agesOfChildren = new ArrayList<Integer>(
				Arrays.asList(agesOfChildren));
	}

	public Integer getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(Integer numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public List<Integer> getAgesOfChildren() {
		return agesOfChildren;
	}

	public void setAgesOfChildren(ArrayList<Integer> agesOfChildren) {
		this.agesOfChildren = agesOfChildren;
	}

	@Override
	public String getValue() {
		StringBuffer toReturn = new StringBuffer();
		toReturn.append(numberOfAdults);
		for (Integer age : agesOfChildren) {
			toReturn.append(",");
			toReturn.append(age);
		}
		return toReturn.toString();
	}
}