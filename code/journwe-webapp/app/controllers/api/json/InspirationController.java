package controllers.api.json;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.typesafe.config.ConfigFactory;
import models.UserManager;
import models.dao.inspiration.InspirationTipDAO;
import models.dao.inspiration.InspirationURLDAO;
import models.dao.user.UserDAO;
import models.inspiration.InspirationTip;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.inspiration.InspirationURL;
import models.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
        User usr = UserManager.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        if(usr != null) tip.setUserId(usr.getId());
        tip.setActive(false);

        new InspirationTipDAO().save(tip);

        return ok();
    }

    public static Result getImages(String id) {
        List<ObjectNode> images = new ArrayList<ObjectNode>();
        int i = 0;
        for (S3ObjectSummary os : s3.listObjects(new ListObjectsRequest().withBucketName(S3_BUCKET_INSPIRATION_IMAGES).withPrefix(id + "/")).getObjectSummaries()) {
            try {
                s3.setObjectAcl(os.getBucketName(), os.getKey(), CannedAccessControlList.PublicRead);

                ObjectNode node = Json.newObject();
                node.put("index", i);
                node.put("url", URLEncoder.encode(s3.getResourceUrl(S3_BUCKET_INSPIRATION_IMAGES,
                        os.getKey()), "UTF-8"));
                images.add(node);

                i++;
            } catch (UnsupportedEncodingException e) {
                Logger.error("Error while producing public URL of inspiration image from S3.", e);
            }
        }

        return ok(Json.toJson(images));
    }

}
