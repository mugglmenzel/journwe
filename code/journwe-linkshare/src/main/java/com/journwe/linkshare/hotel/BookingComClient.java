package com.journwe.linkshare.hotel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.journwe.linkshare.Client;

/**
 * Booking.com client.
 * 
 * @author markus
 *
 */
public class BookingComClient extends Client {
	
	private static String DOMAIN = "http://www.booking.com/";
	private String offeringType = "";
	private String country = "";
	private String offeringName = "";
	private String checkin = "";
	private String checkout = "";
	
	public BookingComClient() {
		// Our booking.com affiliate id.
		this("383653");
	}

	public BookingComClient(String affiliateId) {
		super(affiliateId);
	}
	
	public void extractPastedLink(final String pastedLink) {
		String[] split1 = pastedLink.split(DOMAIN);
		String split1b = split1[1];
		String[] split2 = split1b.split("/");
		// Extract offering type, e.g., "hotel"
		offeringType = split2[0];
		// Extract country, e.g. "us"
		country = split2[1];
		String split2c = split2[2];
		String[] split3 = split2c.split("\\?");
		// Extract offering name, e.g. "holiday-inn-club-vacation ..."
		offeringName = split3[0];
		String split3b = split3[1];
		// Extract checkin date
		Pattern pattern = Pattern.compile("checkin=(.*?);");
		Matcher matcher = pattern.matcher(split3b);
		if (matcher.find())
		{
		    //System.out.println("Found:"+matcher.group(1));
			checkin = matcher.group(1);
		}
		// Extract checkout date
		pattern = Pattern.compile("checkout=(.*?);");
		matcher = pattern.matcher(split3b);
		if (matcher.find())
		{
		    //System.out.println("Found:"+matcher.group(1));
			checkout = matcher.group(1);
		}
	}
	
	public String createLinkForSharing() {
		StringBuffer link = new StringBuffer();
		link.append(DOMAIN);
		link.append(offeringType);
		link.append("/");
		link.append(country);
		link.append("/");
		link.append(offeringName);
		link.append("?");
		link.append("checkin=");
		link.append(checkin);
		link.append(";");
		link.append("checkout=");
		link.append(checkout);
		link.append(";");
		return link.toString();
	}
	
	public String toString() {
		return "Offering Type: "+offeringType+"\n"+"Country: "+country+"\n"+"Hotel: "+offeringName+"\nCheckin: "+checkin+"\nCheckout: "+checkout;
	}

	/**
	 * For testing purposes.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		BookingComClient client = new BookingComClient();
		client.extractPastedLink("http://www.booking.com/hotel/us/holiday-inn-club-vacation-las-vegas-at-desert-club-resort.de.html?sid=73e13078d67460ab162c453a787c439d;dcid=1;checkin=2014-04-05;checkout=2014-04-11;ucfs=1;srfid=92a24a8cacbb6c6612a29d92b0e5f0b7c90b400cX1");
		System.out.println(client.toString());
		System.out.println(client.createLinkForSharing());
	}

}
