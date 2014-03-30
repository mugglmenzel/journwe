package controllers.core.html;

import controllers.routes;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mugglmenzel on 26/03/14.
 */
public class CDNAssetsController extends Controller {

    public static final String CLOUDFRONT_SERVER_BASE_URL = "assets-cdn.journwe.com";


    public static Result at(String file) {
        return Play.isProd() ? redirect("http://" + CLOUDFRONT_SERVER_BASE_URL + "/" + file + (Arrays.toString(request().headers().get(HttpHeaders.Names.ACCEPT_ENCODING)).contains("gzip") ? ".gz" : "")) : redirect(routes.Assets.at(file));
    }

}
