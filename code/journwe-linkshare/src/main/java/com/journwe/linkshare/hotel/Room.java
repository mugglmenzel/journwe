package com.journwe.linkshare.hotel;

public class Room {

	private int numberAdults;
	private int numberChildren;
	
	public Room(int numberAdults, int numberChildren) {
		this.numberAdults = numberAdults;
		this.numberChildren = numberChildren;
	}
	
	public int getNumberAdults() {
		return numberAdults;
	}
	public void setNumberAdults(int numberAdults) {
		this.numberAdults = numberAdults;
	}
	public int getNumberChildren() {
		return numberChildren;
	}
	public void setNumberChildren(int numberChildren) {
		this.numberChildren = numberChildren;
	}
	
	
}
