package com.journwe.linkshare.flight;

import java.util.Map.Entry;

import com.journwe.linkshare.hotel.Room;

public class ExpediaClient extends FlightClient {

	public ExpediaClient(String affiliateId) {
		super(affiliateId);
	}

	public ExpediaClient() {
		super("455210");
	}

	public ExpediaFlight extractPastedLink(String pastedLink) {
		ExpediaFlight toReturn = new ExpediaFlight();
		if (pastedLink.startsWith("http://www.expedia.de/Flight-Search-All?")) {
			String[] split1 = pastedLink
					.split("http://www.expedia.de/Flight-Search-All?");
			String afterQuestionMarkString = split1[1];
			// Extract &-separated parameters
			String[] split2 = afterQuestionMarkString.split("&");
			for (String param : split2) {
				String[] keyValuePair = param.split("=");
				if (keyValuePair.length == 2) {
					String key = keyValuePair[0];
					String value = keyValuePair[1];
					if (toReturn.containsKey(key)) {
						toReturn.put(key, value);
					}
				}
			}
		} else if (pastedLink
				.startsWith("http://www.expedia.de/Flights-Search?")) {
			String[] split1 = pastedLink
					.split("http://www.expedia.de/Flights-Search?");
			String afterQuestionMarkString = split1[1];
			// Extract &-separated parameters
			String[] split2 = afterQuestionMarkString.split("&");
			for (String param : split2) {
				String[] keyValuePair = param.split("=");
				String key = keyValuePair[0];
				String value = keyValuePair[1];
				if (key.startsWith("leg")) {
					if (key.equals("leg1")) {
						String[] leg1 = value.split(",");
						toReturn.put("inpDepartureLocations",
								leg1[0].split(":")[1]);
						toReturn.put("inpArrivalLocations",
								leg1[1].split(":")[1]);
						String departureDate = leg1[2].split(":")[1].replace(
								".", "-").substring(0,
								(leg1[2].split(":")[1].length() - 4));
						toReturn.put("inpDepartureDates", departureDate);
					}
					if (key.equals("leg2")) {
						String[] leg2 = value.split(",");
						String returnDate = leg2[2].split(":")[1].replace(".",
								"-").substring(0,
								(leg2[2].split(":")[1].length() - 4));
						toReturn.put("inpArrivalDates", returnDate);
					}
					if (key.equals("passengers")) { // &passengers=children:0,adults:4,seniors:0,infantinlap:Y
						String[] passengers = value.split(",");
						toReturn.put("inpChildCounts",
								passengers[0].split(":")[1]);
						toReturn.put("inpAdultCounts",
								passengers[1].split(":")[1]);
						toReturn.put("inpChildCounts",
								passengers[0].split(":")[1]);
					}
				}
			}
		}
		return toReturn;
	}

	public String createLinkForSharing(final ExpediaFlight flight) {
		StringBuffer link = new StringBuffer();
		link.append("http://www.expedia.de/Flight-Search-All?action=FlightSearchAll%40searchFlights&origref=www.journwe.com");
		for (Entry<String, String> e : flight.entrySet()) {
			link.append("&");
			link.append(e.getKey());
			link.append("=");
			link.append(e.getValue());
		}
		// return constructed link
		return link.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExpediaClient client = new ExpediaClient();
		ExpediaFlight flight1 = client
				.extractPastedLink("http://www.expedia.de/Flights-Search?trip=roundtrip&leg1=from:HAM,to:FRA,departure:11.05.2014TANYT&leg2=from:FRA,to:HAM,departure:14.05.2014TANYT&passengers=children:0,adults:4,seniors:0,infantinlap:Y&options=cabinclass:coach&mode=search&");
		ExpediaFlight flight2 = client
				.extractPastedLink("http://www.expedia.de/Flight-Search-All?action=FlightSearchAll%40searchFlights&origref=www.expedia.de%2FFlight-Search-All&inpFlightRouteType=2&inpDepartureLocations=Hamburg+%28HAM-Alle+Flugh%C3%A4fen%29&inpArrivalLocations=Frankfurt+am+Main+%28FRA-Alle+Flugh%C3%A4fen%29&inpDepartureDates=11-05-2014&inpArrivalDates=14-05-2014&inpAdultCounts=4&inpChildCounts=0&inpChildAges=-1&inpChildAges=-1&inpChildAges=-1&inpChildAges=-1&inpChildAges=-1&inpInfants=2&inpFlightAirlinePreference=&inpFlightClass=3");
		System.out.println("Flight1: " + client.createLinkForSharing(flight1));
		System.out.println("Flight2: " + client.createLinkForSharing(flight2));
	}

}
