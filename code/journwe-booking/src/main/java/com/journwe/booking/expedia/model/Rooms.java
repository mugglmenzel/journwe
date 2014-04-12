package com.journwe.booking.expedia.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.journwe.booking.expedia.model.common.AbstractParameterModel;

public class Rooms extends AbstractParameterModel {

	private List<Room> rooms = new ArrayList<Room>();

	public Rooms(Room... rooms) {
		this.rooms = new ArrayList<Room>(Arrays.asList(rooms));
	}
	
	@Override
	public String getValue() {
		StringBuffer toReturn = new StringBuffer();
		for (int i=1; i<=rooms.size(); i++) {
			toReturn.append("&room");
			toReturn.append(i);
			toReturn.append("=");
			toReturn.append(rooms.get(i-1).getValue());
		}
		return toReturn.toString();
	}

}
