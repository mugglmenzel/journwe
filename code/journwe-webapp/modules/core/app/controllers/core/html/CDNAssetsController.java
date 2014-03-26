package controllers.core.html;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by mugglmenzel on 26/03/14.
 */
public class CDNAssetsController extends Controller {

    public static final String CLOUDFRONT_SERVER_BASE_URL = "assets-cdn.journwe.com";


    public static Result at(String file) {
        return redirect("http://" + CLOUDFRONT_SERVER_BASE_URL + "/public/" + file);
    }

}
