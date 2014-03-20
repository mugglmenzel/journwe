package com.journwe.linkshare.hotel;

import java.util.ArrayList;
import java.util.List;

import com.journwe.linkshare.Client;

public class ExpediaClient extends Client {
	
	private static final String DOMAIN = "http://www.expedia.de/";
	private String expediaOfferingName = ""; // name of the hotel
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
		// Extract offering name, e.g., "Las-Vegas-Hotels-Stratosphere-Hotel-Casino..."
		expediaOfferingName = split1b.split("\\?")[0];
		String afterQuestionMarkString = split1b.split("\\?")[1];
		String[] split3 = afterQuestionMarkString.split("&");
		for (String param : split3) {
			// when to check in and check out
			if (param.startsWith("chkin="))
				checkin = param.split("chkin=")[1];
			if (param.startsWith("chkout="))
				checkout = param.split("chkout=")[1];
			// assume, no kids
			if (param.startsWith("rm")) {
				Integer adultsCount = new Integer(
						param.split("rm\\d=a")[1]);
				rooms.add(new Room(adultsCount, 0));
			}

		}
	}

	@Override
	public String createLinkForSharing() {
		StringBuffer link = new StringBuffer();
		link.append(DOMAIN);
		link.append(expediaOfferingName);
		link.append("?chkin=");
		link.append(checkin);
		link.append("&chkout=");
		link.append(checkout);
		if (rooms.size() > 0) {
			int roomNumber = 1;
			for (Room r : rooms) {
				link.append("&rm");
				link.append(roomNumber);
				link.append("=a");
				link.append(r.getNumberAdults());
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
		searchLink.append("Hotel-Search#");
		searchLink.append("destination=");
		searchLink.append(destination); // e.g. Las+Vegas%2C+NV%2C+United+States
		searchLink.append("&startDate=");
		searchLink.append(checkin); // e.g. 30.03.2014
		searchLink.append("&endDate=");
		searchLink.append(checkout); // e.g. 24.04.2014
		for (Room r : rooms) {
			searchLink.append("&adults=");
			searchLink.append(r.getNumberAdults());
		}
		
		// TODO
		// append affiliate id
//		searchLink.append(this.getAffiliateId());

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
				"26.06.2014", "29.06.2014");
		System.out.println(searchLink);
		
		client.extractPastedLink("http://www.expedia.de/Las-Vegas-Hotels-Stratosphere-Hotel-Casino-Resort-Hotel.h41081.Hotel-Beschreibung?chkin=30.03.2014&chkout=24.04.2014&rm1=a3&rm2=a2&rm3=a2&hwrqCacheKey=5e7211bb-3f14-4eb2-975f-8c8d793a9e7fHWRQ1395308667971&c=2e4671cd-712a-40c6-bb76-f82864a2d322&&hashTag=default&rfrr=-30461");
		System.out.println(client.createLinkForSharing());
	}

}
