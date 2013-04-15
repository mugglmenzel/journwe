package controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

import controllers.auth.SecuredUser;
import controllers.auth.oauth2.fb.FacebookAuthProvider;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import views.html.fb_test;

public class FbController extends Controller {
	
	@Security.Authenticated(SecuredUser.class)
	public static Result test() {
		final String accessToken = FacebookAuthProvider.ACCESS_TOKEN;
		Logger.debug("Probably not the most secure way to use this token: "+accessToken);
		FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
		User user = facebookClient.fetchObject("me", User.class);
		return ok(fb_test.render(user.getName()));
	}

}
