package models.helpers;

import com.typesafe.config.ConfigFactory;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.sasl.SASLMechanism;
import play.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: markus
 * Date: 7/8/13
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class JournweFacebookChatClient {
    public static final String XMPP_HOST = "chat.facebook.com";
    public static final int XMPP_PORT = 5222;
    private String clientId = ConfigFactory.load().getString("play-authenticate.facebook.clientId");
    private XMPPConnection connection;

    public void sendMessage(final String accessToken, final String messageText, final String destinationUser) {
        connection = JournweFacebookChatClient.createXMPPConnection();
        try {
            connection.connect();
            connection.login(clientId, accessToken);
            Logger.debug("Connected XMPP user: " + connection.getUser());

            String to = "-"+destinationUser+"@"+XMPP_HOST;
            Logger.debug("Send XMPP message to: " + to);
            Chat chat = connection.getChatManager().createChat(to, null);
            chat.sendMessage(messageText);

        } catch (XMPPException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
    }

    public static synchronized XMPPConnection createXMPPConnection() {
        SASLAuthentication.registerSASLMechanism(
                SASLXFacebookPlatformMechanism.NAME,
                SASLXFacebookPlatformMechanism.class);
        SASLAuthentication.supportSASLMechanism(
                SASLXFacebookPlatformMechanism.NAME, 0);

        ConnectionConfiguration configuration = new ConnectionConfiguration(
                XMPP_HOST, XMPP_PORT);
        configuration.setSASLAuthenticationEnabled(true);

        return new XMPPConnection(configuration);
    }

    public static class SASLXFacebookPlatformMechanism extends SASLMechanism {
        public static final String NAME = "X-FACEBOOK-PLATFORM";
        private String apiKey = "";
        private String accessToken = "";

        public SASLXFacebookPlatformMechanism(
                SASLAuthentication saslAuthentication) {
            super(saslAuthentication);
        }

        @Override
        protected void authenticate() throws IOException, XMPPException {
            AuthMechanism stanza = new AuthMechanism(getName(), null);
            getSASLAuthentication().send(stanza);
        }

        @SuppressWarnings("hiding")
        @Override
        public void authenticate(String apiKey, String host, String accessToken)
                throws IOException, XMPPException {
            if (apiKey == null || accessToken == null) {
                throw new IllegalStateException("Invalid parameters!");
            }

            this.apiKey = apiKey;
            this.accessToken = accessToken;
            this.hostname = host;

            String[] mechanisms = {"DIGEST-MD5"};
            Map<String, String> props = new HashMap<String, String>();
            this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host,
                    props, this);
            authenticate();
        }

        @Override
        public void authenticate(String username, String host,
                                 CallbackHandler cbh) throws IOException, XMPPException {
            String[] mechanisms = {"DIGEST-MD5"};
            Map<String, String> props = new HashMap<String, String>();
            this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host,
                    props, cbh);
            authenticate();
        }

        @Override
        protected String getName() {
            return NAME;
        }

        @Override
        public void challengeReceived(String challenge) throws IOException {
            byte response[] = null;
            if (challenge != null) {
                String decodedResponse = new String(
                        org.jivesoftware.smack.util.Base64.decode(challenge));
                Map<String, String> parameters = getQueryMap(decodedResponse);

                String version = "1.0";
                String nonce = parameters.get("nonce");
                String method = parameters.get("method");

                Long callId = Long.valueOf(System.currentTimeMillis());

                String composedResponse = String
                        .format(
                                "method=%s&nonce=%s&access_token=%s&api_key=%s&call_id=%s&v=%s",
                                URLEncoder.encode(method, "UTF-8"),
                                URLEncoder.encode(nonce, "UTF-8"),
                                URLEncoder.encode(this.accessToken, "UTF-8"),
                                URLEncoder.encode(this.apiKey, "UTF-8"),
                                callId, URLEncoder.encode(version, "UTF-8"));
                response = composedResponse.getBytes();
            }

            String authenticationText = "";

            if (response != null) {
                authenticationText = org.jivesoftware.smack.util.Base64
                        .encodeBytes(
                                response,
                                org.jivesoftware.smack.util.Base64.DONT_BREAK_LINES);
            }

            Response stanza = new Response(authenticationText);

            getSASLAuthentication().send(stanza);
        }

        private Map<String, String> getQueryMap(String query) {
            String[] params = query.split("&");
            Map<String, String> map = new HashMap<String, String>();
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
            return map;
        }
    }
}