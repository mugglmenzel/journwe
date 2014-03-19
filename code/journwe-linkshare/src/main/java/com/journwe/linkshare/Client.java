package com.journwe.linkshare;

public abstract class Client {

	private String affiliateId;

	public Client(String affiliateId) {
		this.affiliateId = affiliateId;
	}

	/**
	 * The link that an adventurer has found, e.g., on booking.com and posts in
	 * a JournWe. Extract information from this link to create a link for
	 * sharing.
	 */
	public abstract void extractPastedLink(final String pastedLink);

	/**
	 * Create a link that can be shared with other JournWe adventurers.
	 * 
	 * @return link that others can click on to book an offer
	 */
	public abstract String createLinkForSharing();

	public String getAffiliateId() {
		return affiliateId;
	}

	public void setAffiliateId(String affiliateId) {
		this.affiliateId = affiliateId;
	}

}
