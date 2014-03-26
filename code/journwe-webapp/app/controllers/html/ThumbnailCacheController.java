package controllers.html;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.typesafe.config.ConfigFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import play.cache.Cache;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

import static play.data.Form.form;

/**
 * Created by mugglmenzel on 26/03/14.
 */
public class ThumbnailCacheController extends Controller {

    public static final String S3_BUCKET_THUMBNAILS_CACHE = "journwe-thumbnails-cache";

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

        if(width == null && height == null) return badRequest();

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
                    return s3.generatePresignedUrl(S3_BUCKET_THUMBNAILS_CACHE, toS3Key(width, height, timestamp, url), DateTime.now().plusHours(24).toDate()).toString();
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
        return url + "_" + width + "x" + height + "_" + timestamp;
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
