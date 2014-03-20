package com.journwe.linkshare.hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.journwe.linkshare.Client;

public class ExpediaClient extends Client {

	private static final String DOMAIN = "http://www.travelnow.com/templates/";
	private String offeringType = "hotels";
	private String expediaOfferingId = ""; // id of the hotel
	private String checkin;
	private String checkout;

	List<Room> rooms = new ArrayList<Room>();

	public ExpediaClient(String affiliateId) {
		super(affiliateId);
	}

	public ExpediaClient() {
		super("455210");
	}

	@Override
	public void extractPastedLink(String pastedLink) {
		String[] split1 = pastedLink.split(DOMAIN);
		String split1b = split1[1];
		String[] split2 = split1b.split("/");
		// Extract offering type, e.g., "hotels"
		offeringType = split2[1];
		expediaOfferingId = split2[2];
		String afterQuestionMarkString = split2[3].split("overview?")[1];
		String[] split3 = afterQuestionMarkString.split("&");
		for (String param : split3) {
			// when to check in and check out
			if (param.startsWith("standardCheckin="))
				checkin = param.split("standardCheckin=")[1];
			if (param.startsWith("standardCheckout="))
				checkout = param.split("standardCheckout=")[1];
			// assume, no kids
			if (param.startsWith("rooms[") && param.contains("adultsCount")) {
				Integer adultsCount = new Integer(
						param.split("rooms\\[\\d\\]\\.adultsCount=")[1]);
				rooms.add(new Room(adultsCount, 0));
			}

		}
		// How many rooms and how many people?
		// Pattern pattern = Pattern.compile("(rooms\\[\\d\\]\\.)(.+)(\\&)");
		// Matcher matcher = pattern.matcher(afterQuestionMarkString);
		// if (matcher.find())
		// {
		// System.out.println("Found:"+matcher.group(0));
		// System.out.println("Found:"+matcher.group(1));
		// System.out.println("Found:"+matcher.group(2));
		// System.out.println("Found:"+matcher.group(3));
		// System.out.println("matcher.groupCount(): "+matcher.groupCount());
		// }
	}

	@Override
	public String createLinkForSharing() {
		StringBuffer link = new StringBuffer();
		link.append(DOMAIN);
		link.append(getAffiliateId());
		link.append("/");
		link.append(offeringType);
		link.append("/");
		link.append(expediaOfferingId);
		link.append("/overview?");
		link.append("standardCheckin=");
		link.append(checkin);
		link.append("&");
		link.append("standardCheckout=");
		link.append(checkout);
		if (rooms.size() > 0) {
			link.append("&roomsCount=");
			link.append(rooms.size());
			int roomNumber = 0;
			for (Room r : rooms) {
				link.append("&rooms[");
				link.append(roomNumber);
				link.append("].adultsCount=");
				link.append(r.getNumberAdults());
				link.append("&rooms[");
				link.append(roomNumber);
				link.append("].childrenCount=0");
				roomNumber++;
			}
		}
		// return constructed link
		return link.toString();
	}

	public String createLinkForSearching(final String destination,
			final List<Room> rooms, final String checkin, final String checkout) {
		StringBuffer searchLink = new StringBuffer();
		searchLink.append(DOMAIN);
		// append affiliate id
		searchLink.append(this.getAffiliateId());
		searchLink.append("/");
		searchLink.append(offeringType);
		searchLink.append("/");
		searchLink.append("list?");
		searchLink.append("destination=");
		searchLink.append(destination); // eg. Las+Vegas%2C+NV%2C+United+States
		searchLink.append("&roomsCount=");
		searchLink.append(rooms.size());
		int i = 0;
		for (Room r : rooms) {
			searchLink.append("&rooms[");
			searchLink.append(i);
			searchLink.append("].adultsCount=");
			searchLink.append(r.getNumberAdults());
			searchLink.append("&rooms[");
			searchLink.append(i);
			searchLink.append("].childrenCount=");
			searchLink.append(r.getNumberChildren());
			i++;
		}
		searchLink.append("&checkin=");
		searchLink.append(checkin); // e.g. 6%2F26%2F14
		searchLink.append("&checkout=");
		searchLink.append(checkout); // e.g. 6%2F29%2F14

		// return constructed link
		return searchLink.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExpediaClient client = new ExpediaClient();
		List<Room> rooms = new ArrayList<Room>();
		rooms.add(new Room(3, 0));
		rooms.add(new Room(2, 0));
		rooms.add(new Room(2, 0));
		String searchLink = client.createLinkForSearching("Las+Vegas", rooms,
				"6%2F26%2F14", "6%2F29%2F14");
		System.out.println(searchLink);

		client.extractPastedLink("http://www.travelnow.com/templates/455210/hotels/119566/overview?lang=en&currency=EUR&secureUrlFromDataBridge=https%3A%2F%2Fwww.travelnow.com&requestVersion=V2&checkin=6%2F26%2F14&checkout=6%2F29%2F14&creditCardInfo.needStartDate=true&destination=Las%2BVegas%2C%2BNV%2C%2BUnited%2BStates&filter.highPrice=2147483647&filter.lowPrice=0&roomsCount=3&rooms[0].adultsCount=3&rooms[0].childrenCount=0&rooms[1].adultsCount=2&rooms[1].childrenCount=0&rooms[2].adultsCount=2&rooms[2].childrenCount=0&standardCheckin=6%2F26%2F2014&standardCheckout=6%2F29%2F2014&targetId=AREA-4fded4ff-af4a-41b7-976b-bd5bd436f135|cities&linkId=HotSearch:Hot:ResultsList:Details&pagename=ToPropDetails");
		System.out.println(client.createLinkForSharing());
	}

}
