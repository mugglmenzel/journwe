package com.journwe.flight.skyscanner.response;

import java.util.List;

public class PricingOption {
	
	private List<Integer> agents;
	private Integer quoteAgeInMinutes;
	private Double price;

	public PricingOption(List<Integer> agents, Integer quoteAgeInMinutes,
			Double price) {
		super();
		this.agents = agents;
		this.quoteAgeInMinutes = quoteAgeInMinutes;
		this.price = price;
	}
	
	public List<Integer> getAgents() {
		return agents;
	}
	public void setAgents(List<Integer> agents) {
		this.agents = agents;
	}
	public Integer getQuoteAgeInMinutes() {
		return quoteAgeInMinutes;
	}
	public void setQuoteAgeInMinutes(Integer quoteAgeInMinutes) {
		this.quoteAgeInMinutes = quoteAgeInMinutes;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

	
}
