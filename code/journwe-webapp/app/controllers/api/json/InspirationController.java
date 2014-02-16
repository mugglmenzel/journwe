package controllers.api.json;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.typesafe.config.ConfigFactory;
import models.auth.SecuredAdminUser;
import models.auth.SecuredUser;
import models.category.Category;
import models.dao.category.CategoryDAO;
import models.dao.inspiration.InspirationDAO;
import models.dao.inspiration.InspirationTipDAO;
import models.dao.inspiration.InspirationURLDAO;
import models.dao.manytomany.CategoryToInspirationDAO;
import models.dao.user.UserDAO;
import models.dao.user.UserSocialDAO;
import models.inspiration.Inspiration;
import models.inspiration.InspirationTip;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.inspiration.InspirationURL;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index.indexNew;
import views.html.inspiration.get;
import views.html.inspiration.manage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

public class InspirationController extends Controller {

    private static final String S3_BUCKET_INSPIRATION_IMAGES = "journwe-inspiration-images";

    private static AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey")));

    public static Result getUrls(String id) {
        DynamicForm data = form().bindFromRequest();
        final String lastId = data.get("lastId");
        final int count = data.get("count") != null ? new Integer(data.get("count")).intValue() : 10;

        List<JsonNode> result = new ArrayList<JsonNode>();
        for (InspirationURL url : new InspirationURLDAO().all(id, lastId, count)) {
            result.add(Json.toJson(url));
        }

        return ok(Json.toJson(result));
    }

    public static Result getTips(String id) {
        DynamicForm data = form().bindFromRequest();
        final String lastId = data.get("lastId");
        final int count = data.get("count") != null ? new Integer(data.get("count")).intValue() : 5;

        List<ObjectNode> result = new ArrayList<ObjectNode>();
        for (InspirationTip tip : new InspirationTipDAO().activated(id, lastId, count)) {
            ObjectNode node = Json.newObject();
            node.put("user", Json.toJson(new UserDAO().get(tip.getUserId())));
            node.put("tip", Json.toJson(tip));
            result.add(node);
        }

        return ok(Json.toJson(result));
    }

    public static Result addTip(String id) {
        DynamicForm data = form().bindFromRequest();
        final String tipTxt = data.get("tip");
        InspirationTip tip = new InspirationTip();
        tip.setInspirationId(id);
        tip.setCreated(new Date().getTime());
        tip.setTip(tipTxt);
        tip.setLang(lang().code());
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        tip.setUserId(new UserSocialDAO().findBySocialId(usr.getProvider(), usr.getId()).getUserId());
        tip.setActive(false);

        new InspirationTipDAO().save(tip);

        return ok();
    }

    public static Result getImages(String id) {
        List<String> images = new ArrayList<String>();
        for (S3ObjectSummary os : s3.listObjects(new ListObjectsRequest().withBucketName(S3_BUCKET_INSPIRATION_IMAGES).withPrefix(id + "/")).getObjectSummaries()) {
            try {
                s3.setObjectAcl(os.getBucketName(), os.getKey(), CannedAccessControlList.PublicRead);
                images.add(URLEncoder.encode(s3.getResourceUrl(S3_BUCKET_INSPIRATION_IMAGES,
                        os.getKey()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Logger.error("Error while producing public URL of inspiration image from S3.", e);
            }
        }

        return ok(Json.toJson(images));
    }

}
