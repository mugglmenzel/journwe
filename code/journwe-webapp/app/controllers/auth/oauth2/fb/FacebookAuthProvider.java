package controllers.auth.oauth2.fb;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.dao.UserSocialDAO;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.codehaus.jackson.JsonNode;

import play.Application;
import play.Logger;
import play.libs.WS;
import play.libs.WS.Response;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.exceptions.AccessTokenException;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.oauth2.OAuth2AuthProvider;

public class FacebookAuthProvider extends
		OAuth2AuthProvider<FacebookAuthUser, FacebookAuthInfo> {

	private static final String MESSAGE = "message";
	private static final String ERROR = "error";
	private static final String FIELDS = "fields";
	
	public static String ACCESS_TOKEN = "";

	static final String PROVIDER_KEY = "facebook";
	
	private static final String USER_INFO_URL_SETTING_KEY = "userInfoUrl";
	private static final String USER_INFO_FIELDS_SETTING_KEY = "userInfoFields";

	public FacebookAuthProvider(Application app) {
		super(app);
	}

	@Override
	protected FacebookAuthUser transform(FacebookAuthInfo info, final String state)
			throws AuthException {

		final String url = getConfiguration().getString(
				USER_INFO_URL_SETTING_KEY);
		final String fields = getConfiguration().getString(
				USER_INFO_FIELDS_SETTING_KEY);
		final Response r = WS
				.url(url)
				.setQueryParameter(OAuth2AuthProvider.Constants.ACCESS_TOKEN,
						info.getAccessToken())
				.setQueryParameter(FIELDS, fields)
				.get().get(PlayAuthenticate.TIMEOUT);
		// @markusklems Modification of default plugin: Save access token
		final String facebookId = r.asJson().get("id").getTextValue();
		final String accessToken = info.getAccessToken();
		Logger.debug("Facebook ID: "+facebookId);	
		Logger.debug("access_token: "+accessToken);
		new UserSocialDAO().saveFacebookAccessToken(facebookId,accessToken);

		final JsonNode result = r.asJson();
		if (result.get(OAuth2AuthProvider.Constants.ERROR) != null) {
			throw new AuthException(result.get(ERROR).get(MESSAGE).asText());
		} else {
			Logger.debug(result.toString());
			return new FacebookAuthUser(result, info, state);
		}
	}

	@Override
	public String getKey() {
		return PROVIDER_KEY;
	}

	@Override
	protected FacebookAuthInfo buildInfo(final Response r)
			throws AccessTokenException {
		if (r.getStatus() >= 400) {
			throw new AccessTokenException(r.asJson().get(ERROR).get(MESSAGE).asText());
		} else {
			final String query = r.getBody();
			Logger.debug(query);
			final List<NameValuePair> pairs = URLEncodedUtils.parse(
					URI.create("/?" + query), "utf-8");
			if (pairs.size() < 2) {
				throw new AccessTokenException();
			}
			final Map<String, String> m = new HashMap<String, String>(
					pairs.size());
			for (final NameValuePair nameValuePair : pairs) {
				String name = nameValuePair.getName();
				String value = nameValuePair.getValue();
				m.put(name, value);
			}

			return new FacebookAuthInfo(m);
		}
	}

}
