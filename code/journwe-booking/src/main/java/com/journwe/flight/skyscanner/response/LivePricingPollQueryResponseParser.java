package com.journwe.flight.skyscanner.response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class LivePricingPollQueryResponseParser {
	
	public static List<Itinerary> getItineraries(JsonNode livePricingPollQueryResponse) {
		List<Itinerary> toReturn = new ArrayList<Itinerary>();
		JsonNode itineraries = livePricingPollQueryResponse.get("Itineraries");		
		// returns a list of itineraries, e.g., [{"OutboundLegId":"11235-1406102035-BA-0-13554-1406102155","InboundLegId" ...
		Iterator<JsonNode> itinerariesIterator = itineraries.elements();		
		while(itinerariesIterator.hasNext()) {
			JsonNode jsonItinerary = itinerariesIterator.next();
			// An itinerary has three important pieces of information:
			// * outboundLegId
			// * inboundLegId
			// * List of pricingOptions
			String outboundLegId = jsonItinerary.get("OutboundLegId").asText();
			String inboundLegId = jsonItinerary.get("InboundLegId").asText();
			List<PricingOption> pricingOptions = new ArrayList<PricingOption>();
			Iterator<JsonNode> pricingOptionsIterator = jsonItinerary.get("PricingOptions").elements();	
			while(pricingOptionsIterator.hasNext()) {
				JsonNode jsonPricingOption = pricingOptionsIterator.next();
				Integer quoteAgeInMinutes = jsonPricingOption.get("QuoteAgeInMinutes").asInt();
				Double price = jsonPricingOption.get("Price").asDouble();
				Iterator<JsonNode> agentsIterator = jsonPricingOption.get("Agents").elements();
				List<Integer> agents = new ArrayList<Integer>();
				while(agentsIterator.hasNext()) {
					Integer agent = agentsIterator.next().asInt();
					agents.add(agent);
				}
				PricingOption pricingOption = new PricingOption(agents,quoteAgeInMinutes,price);
				pricingOptions.add(pricingOption);
			}
			toReturn.add(new Itinerary(outboundLegId, inboundLegId, pricingOptions));
		}
		return toReturn;
	}


}
