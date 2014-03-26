package controllers.html;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import play.Logger;
import play.api.Play;
import play.cache.Cache;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.concurrent.Callable;

import static play.data.Form.form;

/**
 * Created by mugglmenzel on 26/03/14.
 */
public class ThumbnailCacheController extends Controller {

    private static final String privateKeyPath = "conf/pk-APKAIIQOD6MCIRX3BIZA.der";
    private static final String cloudFrontKeyPairId = "APKAIIQOD6MCIRX3BIZA";

    public static final String S3_BUCKET_THUMBNAILS_CACHE = "journwe-thumbnails-cache";
    public static final String CLOUDFRONT_SERVER_BASE_URL = "thumbnails-cdn.journwe.com";

    private static final AWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey"));

    private static final AmazonS3Client s3 = new AmazonS3Client(credentials);

    public static Result getThumbnail() {

        DynamicForm data = form().bindFromRequest();
        final String width = data.get("w");
        final String height = data.get("h");
        final String timestamp = data.get("t");
        final String url = data.get("u");

        if (width == null && height == null) return badRequest();

        if (width != null)
            try {
                new Integer(width);
            } catch (Exception e) {
                e.printStackTrace();
                return badRequest();
            }
        if (height != null)
            try {
                new Integer(height);
            } catch (Exception e) {
                e.printStackTrace();
                return badRequest();
            }
        if (timestamp != null)
            try {
                new Long(timestamp);
            } catch (Exception e) {
                e.printStackTrace();
                return badRequest();
            }

        try {
            new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest();
        }


        try {
            s3.getObjectMetadata(S3_BUCKET_THUMBNAILS_CACHE, toS3Key(width, height, timestamp, url));

            return redirect(Cache.getOrElse(toS3Key(width, height, timestamp, url), new Callable<String>() {
                @Override
                public String call() throws Exception {
                    long expiration = DateTime.now().plusHours(24).toDate().getTime()/1000;
                    String cfPolicy = ("{\"Statement\":[{\"Resource\":\"" + "http://" + CLOUDFRONT_SERVER_BASE_URL + "/" + toS3Key(width, height, timestamp, url) + "\",\"Condition\":{\"DateLessThan\":{\"AWS:EpochTime\":" + expiration + "}}}]}").trim();

                    KeyFactory fac = KeyFactory.getInstance("RSA");
                    EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(FileUtils.readFileToByteArray(Play.getFile(privateKeyPath, Play.current())));
                    PrivateKey privateKey = fac.generatePrivate(privKeySpec);

                    Signature signer = Signature.getInstance("SHA1withRSA");
                    signer.initSign(privateKey);
                    signer.update(cfPolicy.getBytes("UTF-8"));
                    String signature = Base64.encodeBase64String(signer.sign()).replace("+", "-").replace("=", "_").replace("/", "~");
                    return "http://" + CLOUDFRONT_SERVER_BASE_URL + "/" + toS3Key(width, height, timestamp, url) + "?Expires=" + expiration + "&Signature=" + signature + "&Key-Pair-Id=" + cloudFrontKeyPairId;
                    //s3.generatePresignedUrl(S3_BUCKET_THUMBNAILS_CACHE, toS3Key(width, height, timestamp, url), DateTime.now().plusHours(24).toDate()).toString();
                }
            }, 24 * 3600));
        } catch (Exception e) {
            final String embedlyUrl = toEmbedly(width, height, timestamp, url);
            if (embedlyUrl == null) return badRequest();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient httpclient = HttpClients.createDefault();
                        httpclient.execute(new HttpGet(embedlyUrl), new ResponseHandler<Void>() {
                            @Override
                            public Void handleResponse(HttpResponse response) throws IOException {

                                ObjectMetadata meta = new ObjectMetadata();
                                meta.setContentLength(response.getEntity().getContentLength());
                                meta.setContentType(response.getEntity().getContentType().getValue());

                                s3.putObject(S3_BUCKET_THUMBNAILS_CACHE, toS3Key(width, height, timestamp, url), response.getEntity().getContent(), meta);

                                return null;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            return redirect(embedlyUrl);
        }
    }

    private static String toS3Key(String width, String height, String timestamp, String url) {
        try {
            URL urlObj = new URL(url);
            return urlObj.getAuthority() + urlObj.getFile().replace("?", "~") + "_" + width + "x" + height + "_" + timestamp;
        } catch (MalformedURLException e) {
            return url.replace("?", "~") + "_" + width + "x" + height + "_" + timestamp;
        }
    }

    private static String toEmbedly(String width, String height, String timestamp, String url) {
        try {
            StringBuilder sb = new StringBuilder("http://i.embed.ly/1/image/");

            if (height == null || width == null)
                sb.append("resize?" + (width != null ? "width=" + width + "&" : "") + (height != null ? "height=" + height + "&" : ""));
            else
                sb.append("crop?" + (width != null ? "width=" + width + "&" : "") + (height != null ? "height=" + height + "&" : ""));

            sb.append((timestamp != null ? "timestamp=" + timestamp + "&" : "") + "key=2c8ef5b200c6468f9f863bc75c46009f&url=" + URLEncoder.encode(url, "UTF-8"));

            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
