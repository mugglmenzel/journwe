package controllers.api.json;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import models.adventure.Adventure;
import models.auth.SecuredUser;
import models.dao.adventure.AdventureDAO;
import org.joda.time.DateTime;
import play.Logger;
import play.cache.Cache;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AdventurePhotoController extends Controller {

    public static final String S3_BUCKET_ADVENTURE_IMAGES = "journwe-adventure-images";

    private static AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey")));


    @Security.Authenticated(SecuredUser.class)
    public static Result getPhotos(final String advId) {
        try {
            return ok(Cache.getOrElse("adventure." + advId + ".photos.all", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<ObjectNode> images = new ArrayList<ObjectNode>();
                    Adventure adv = new AdventureDAO().get(advId);
                    if (adv == null) return Json.newObject().toString();

                    int i = 0;

                    if (adv.getImage() != null) {
                        ObjectNode node = Json.newObject();
                        node.put("index", i);
                        node.put("id", "title");
                        node.put("url", URLEncoder.encode(adv.getImage(), "UTF-8"));
                        images.add(node);
                        i++;
                    }

                    for (S3ObjectSummary os : s3.listObjects(new ListObjectsRequest().withBucketName(S3_BUCKET_ADVENTURE_IMAGES).withPrefix(advId + "/")).getObjectSummaries()) {
                        try {
                            s3.setObjectAcl(os.getBucketName(), os.getKey(), CannedAccessControlList.BucketOwnerFullControl);
                            String id = os.getKey().substring(os.getKey().lastIndexOf("/") + 1, os.getKey().length());

                            ObjectNode node = Json.newObject();
                            node.put("index", i);
                            node.put("id", id);
                            node.put("url", URLEncoder.encode(s3.generatePresignedUrl(S3_BUCKET_ADVENTURE_IMAGES,
                                    os.getKey(), DateTime.now().plusHours(24).toDate()).toString(), "UTF-8"));
                            if (id != null && !"".equals(id)) images.add(node);

                            i++;
                        } catch (UnsupportedEncodingException e) {
                            Logger.error("Error while producing public URL of adventure photo from S3.", e);
                        }
                    }

                    return Json.toJson(images).toString();
                }
            }, 24 * 3600)).as("application/json");
        } catch (Exception e) {
            Logger.error("Couldn't generate photos for adventure " + advId, e);
            return internalServerError();
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result addPhoto(String advId) {
        try {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart image = body.getFiles().get(0);
            File file = image.getFile();

            if (image.getFilename() != null
                    && !"".equals(image.getFilename()) && file.length() > 0) {
                s3.putObject(new PutObjectRequest(
                        S3_BUCKET_ADVENTURE_IMAGES, advId + "/" + image.getFilename(), file)
                        .withCannedAcl(CannedAccessControlList.BucketOwnerFullControl));
            }
            clearCache(advId);
        } catch (Exception e) {
            return badRequest();
        }

        return ok();
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result deletePhoto(String advId, String photoId) {
        try {
            s3.deleteObject(S3_BUCKET_ADVENTURE_IMAGES, advId + "/" + photoId);
            clearCache(advId);
        } catch (Exception e) {
            return badRequest();
        }

        return ok();
    }

    public static void clearCache(final String advId) {
        Cache.remove("adventure." + advId + ".photos.all");
    }
}
