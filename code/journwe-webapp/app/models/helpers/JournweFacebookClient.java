package models.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import play.Logger;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.FacebookType;
import com.restfb.types.User;

public class JournweFacebookClient {

	private FacebookClient facebookClient;

	public User getFacebookUser(final String facebookUserName) {
		return getFacebookUser(facebookUserName, User.class);
	}

	public JsonObject getFacebookUserAsJson(final String facebookUserName) {
		return getFacebookUser(facebookUserName, JsonObject.class);
	}

	public User getMyFacebookUser() {
		return getFacebookUser("me");
	}

	public JsonObject getMyFacebookUserAsJson() {
		return getFacebookUserAsJson("me");
	}
	
	public List<User> getMyFriends() {
		return getMyFriends(User.class);
	}

	public List<JsonObject> getMyFriendsAsJson() {
		return getMyFriends(JsonObject.class);
	}
	
	/**
	 * 
	 * @param message
	 */
	public void publishMessageOnMyFeed(final String message) {
		final HashMap<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("message", message);
		publishOnMyFeed(paramsMap);
	}
	
	/**
	 * 
	 * @param link
	 */
	public void publishLinkOnMyFeed(final String link) {
		final HashMap<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("link", link);
		publishOnMyFeed(paramsMap);
	}

    /**
     *
     * @param message
     * @param link
     */
    public void publishLinkOnMyFeed(final String message, final String link) {
        final HashMap<String,String> paramsMap = new HashMap<String,String>();
        paramsMap.put("message", message);
        paramsMap.put("link", link);
        publishOnMyFeed(paramsMap);
    }

	/**
	 * 
	 * @param link
	 * @param caption
	 * @param description
	 */
	public void publishLinkOnMyFeed(final String link, String caption, String description) {
		final HashMap<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("link", link);
		paramsMap.put("caption", caption);
		paramsMap.put("description", description);
		publishOnMyFeed(paramsMap);
	}
	
	/**
	 * 
	 * @param link
	 * @param caption
	 * @param description
	 * @param picture
	 */
	public void publishLinkOnMyFeed(final String link, String caption, String description, String picture) {
		final HashMap<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("link", link);
		paramsMap.put("caption", caption);
		paramsMap.put("description", description);
		paramsMap.put("picture", picture);
		publishOnMyFeed(paramsMap);
	}

	/**
	 * 
	 * @param message
	 * @param link
	 * @param caption
	 * @param description
	 * @param picture
	 */
	public void publishLinkOnMyFeed(final String message, final String link, String caption, String description, String picture) {
		final HashMap<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("message", message);
		paramsMap.put("link", link);
		paramsMap.put("caption", caption);
		paramsMap.put("description", description);
		paramsMap.put("picture", picture);
		publishOnMyFeed(paramsMap);
	}
	
	/**
	 * Generic method for posting content on the own feed. See e.g. here: https://developers.facebook.com/docs/reference/api/link/
	 * 
	 * @param paramsMap The first String is the key, the second string is the value.
	 */
	public void publishOnMyFeed(final HashMap<String,String> paramsMap) {
		try {
			Parameter[] params = new Parameter[paramsMap.size()];
			int i=0;
			for(Entry<String,String> e : paramsMap.entrySet()) {
				params[i++] = Parameter.with(e.getKey(), e.getValue());
			}
				
			// Not sure if we need the "publishMessageResponse"
			FacebookType publishMessageResponse = facebookClient.publish(
					"me/feed", FacebookType.class, params);
		} catch (Exception e) {
			// For example: cannot post duplicate messages.
			Logger.warn(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param eventName Name of the event as String
	 * @param eventDescription Description of Event as String
	 * @param eventLocation Location of Event as String
	 * @param startDate Start date of event as standard Java Date object. You should probably set the time zone of the user (?).
	 * @param endDate End date of event as standard Java Date object. You should probably set the time zone of the user (?).
	 * @return eventId You must remember the ID if you want to invite friends to the event.
	 */
	public String createNewEvent(final String eventName,
			final String eventDescription, final String eventLocation,
			final Date startDate, final Date endDate) {
		Logger.debug("Date format of start Date: "+formatDateAsISO8601String(startDate));
		Logger.debug("Date format of end Date: "+formatDateAsISO8601String(endDate));
		FacebookType newEvent = facebookClient.publish("me/events",
				FacebookType.class, Parameter.with("name", eventName),
				Parameter.with("start_time",
						formatDateAsISO8601String(startDate)), Parameter.with(
						"end_time", formatDateAsISO8601String(endDate)),
				Parameter.with("description", eventDescription), Parameter
						.with("location", eventLocation));
		return newEvent.getId();
	}
	
	/**
	 * 
	 * @param eventId The id of the event as it is produced by the <code>createnewEvent</code> method.
	 * @param friendIds The ID of each friend in an array.
	 * @return
	 */
	public boolean inviteFriends(final String eventId, final List<String> friendIds) {
		StringBuffer friendsSb = new StringBuffer(" ");
		int i = 1;
		for(String friendId : friendIds) {
			friendsSb.append(friendId);
			if(i<friendIds.size())
				friendsSb.append(",");
			else
				friendsSb.append(" ");
		}
		Boolean successfullyInvited = facebookClient.publish("/"+eventId+"/invited/", Boolean.class, Parameter.with("users", friendsSb.toString()));
		return successfullyInvited;
	}

	// +++ Helper Methods +++ //

	private static String formatDateAsISO8601String(final Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String dateAsString = df.format(date);
		return dateAsString;
	}

	private <T> T getFacebookUser(final String facebookUserName,
			final Class<T> clazz) {
		T user = facebookClient.fetchObject(facebookUserName, clazz);
		return user;
	}

	public <T> List<T> getMyFriends(final Class<T> clazz) {
		ArrayList<T> toReturn = new ArrayList<T>();
		Connection<T> myFriends = facebookClient.fetchConnection("me/friends",
				clazz);
		for (List<T> userList : myFriends) {
			for (T user : userList) {
				toReturn.add(user);
			}
		}
		return toReturn;
	}

	private void setFacebookClient(FacebookClient facebookClient) {
		this.facebookClient = facebookClient;
	}

	public static JournweFacebookClient create(String accessToken) {
		JournweFacebookClient toReturn = new JournweFacebookClient();
		toReturn.setFacebookClient(new DefaultFacebookClient(accessToken));
		return toReturn;
	}

}
