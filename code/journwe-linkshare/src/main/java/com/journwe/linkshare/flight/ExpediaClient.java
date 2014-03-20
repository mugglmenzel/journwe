package com.journwe.linkshare.flight;

import com.journwe.linkshare.Client;

public class ExpediaClient extends Client {

	public ExpediaClient(String affiliateId) {
		super(affiliateId);
	}
	
	public ExpediaClient() {
		super("455210");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void extractPastedLink(String pastedLink) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String createLinkForSharing() {
		// TODO Auto-generated method stub
		return null;
	}

}
