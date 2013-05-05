package models.helpers;

import models.Weather;
import models.WeatherPeriod;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import play.libs.Json;
import org.codehaus.jackson.JsonNode;

public class WeatherFactory {

	static private Map<String, JsonNode> cache;
	static private Date cacheTime;

	static public Weather getWeather(String place){

		Weather weather = new Weather();

		JsonNode result = retrive(place);

		Iterator<JsonNode> periods = result.get("forecast").get("simpleforecast").get("forecastday").iterator();
	   
	    while ( periods.hasNext() ){
	    	JsonNode period = periods.next();
	    	WeatherPeriod wp = new WeatherPeriod();
	    	JsonNode date = period.get("date");
	    	String month = date.get("month").getIntValue()+"";
	    	String day = date.get("day").getIntValue()+"";
	    	
	    	if (month.length()==1){ month = "0"+month; }
	    	if (day.length()==1){ day = "0"+day; }

	    	wp.setTemperature(period.get("high").get("celsius").getTextValue());
	    	wp.setLowTemperature(period.get("low").get("celsius").getTextValue());
	    	wp.setIcon(period.get("icon").getTextValue().replaceAll("chance", "").replaceAll("nt_", ""));
	    	wp.setCondition(period.get("conditions").getTextValue());
	    	wp.setDate(day+"."+month+"."+date.get("year").getIntValue());
			weather.add(wp);
	    }


		return weather;
	}

	private static JsonNode retrive(String place){

		// Unset cache every day
		if (cache == null || (cacheTime != null && cacheTime.getDay() != new Date().getDay())){ 
			cache = new HashMap<String, JsonNode>();
			cacheTime = new Date();
		}

		if (cache.containsKey(place)){
			return cache.get(place);
		}

		JsonNode result = null;

		try {
			String urlString = "http://api.wunderground.com/api/edba4d8cbd919e59/forecast/q/"+place+".json";
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			String stringResult = getStringFromInputStream(conn.getInputStream());
			result = Json.parse(stringResult);
			cache.put(place, result);
		} catch (MalformedURLException e){

		} catch(IOException i){

		}
		return  result;
	}

	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {
 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}

}
