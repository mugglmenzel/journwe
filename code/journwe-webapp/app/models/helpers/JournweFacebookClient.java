package models.helpers;

import java.util.ArrayList;
import java.util.List;

import play.Logger;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.User;

public class JournweFacebookClient {
	
	FacebookClient facebookClient;
	
	public User getFacebookUser(final String facebookUserName) {
		User user = facebookClient.fetchObject(facebookUserName, User.class);
		Logger.debug("getFacebookUser() -> "+user.getName());
		return user;
	}
	
	public User getMyFacebookUser() {
		return getFacebookUser("me");
	}
	
	public List<User> getMyFriends() {
		ArrayList<User> toReturn = new ArrayList<User>();
		Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
		for(List<User> userList : myFriends) {
			for(User user : userList) {
				toReturn.add(user);
				Logger.debug("getMyFriends() -> "+user.getName());
			}
		}
		return toReturn;
	}
	
	public void publishOnMyFeed(final String text) {
		FacebookType publishMessageResponse =
				  facebookClient.publish("me/feed", FacebookType.class,
				    Parameter.with("message", text));
		Logger.debug("publishOnMyFeed() -> "+publishMessageResponse.toString());

	}

	private FacebookClient getFacebookClient() {
		return facebookClient;
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
