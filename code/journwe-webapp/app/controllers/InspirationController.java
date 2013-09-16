package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.typesafe.config.ConfigFactory;
import models.auth.SecuredAdminUser;
import models.auth.SecuredBetaUser;
import models.dao.CategoryDAO;
import models.dao.InspirationCategoryDAO;
import models.dao.InspirationDAO;
import models.inspiration.Inspiration;
import models.inspiration.InspirationCategory;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
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
import java.util.List;

import static play.data.Form.form;

public class InspirationController extends Controller {

    private static final String S3_BUCKET_INSPIRATION_IMAGES = "journwe-inspiration-images";

    private static Form<Inspiration> insForm = form(Inspiration.class);

    private static AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"),
            ConfigFactory.load().getString("aws.secretKey")));

    public static Result get(String id) {
        Inspiration ins = new InspirationDAO().get(id);
        if (ins == null) return badRequest();

        List<InspirationCategory> cats = new InspirationCategoryDAO().getCategories(ins.getId());
        InspirationCategory cat = cats != null && cats.size() > 0 ? cats.iterator().next() : null;

        return ok(get.render(ins, cat != null ? new CategoryDAO().get(cat.getCategoryId()) : null));
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

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result create() {
        return ok(manage.render(insForm, new CategoryDAO().allOptionsMap(),
                new InspirationDAO().all()));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result createAdventure(String insId) {
        return ok(indexNew.render(new InspirationDAO().get(insId)));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result edit(String id) {
        Form<Inspiration> editInsForm = insForm.fill(new InspirationDAO().get(id));
        return ok(manage.render(editInsForm,
                new CategoryDAO().allOptionsMap(),
                new InspirationDAO().all()));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static String getBucketURL(String id) {
        return s3.getResourceUrl(S3_BUCKET_INSPIRATION_IMAGES,
                id);
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result save() {

        Form<Inspiration> filledInsForm = insForm.bindFromRequest();
        DynamicForm df = form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart image = body.getFile("image");

        if (filledInsForm.hasErrors()) {
            flash("error", "Please fill out the form correctly.");
            return badRequest(manage.render(filledInsForm,
                    new CategoryDAO().allOptionsMap(),
                    new InspirationDAO().all()));
        } else {
            Inspiration ins = filledInsForm.get();

            File file = image != null ? image.getFile() : null;

            try {
                if (ins.getId() == null && !new InspirationDAO().save(ins))
                    throw new Exception();

//                String originalCategoryId = df.get("originalInspirationCategoryId");
//                Logger.debug("original cat: " + originalCategoryId + ", new cat: " + ins.getCategoryId());


                Logger.debug("got image files " + (image != null ? image.getFilename() : "none"));
                if (image != null && image.getFilename() != null
                        && !"".equals(image.getFilename()) && file != null && file.length() > 0) {
                    AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                            ConfigFactory.load().getString("aws.accessKey"),
                            ConfigFactory.load().getString("aws.secretKey")));
                    s3.putObject(new PutObjectRequest(
                            S3_BUCKET_INSPIRATION_IMAGES, ins.getId() + "/title", file)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    ins.setImage(s3.getResourceUrl(S3_BUCKET_INSPIRATION_IMAGES,
                            ins.getId() + "/title"));
                } else
                    ins.setImage(new InspirationDAO().get(ins.getId()).getImage());


                if (form().bindFromRequest().get("place") != null) {
                    String place = form().bindFromRequest().get("place");
                    ins.setPlaceAddress(place.split("/")[0]);
                    ins.setPlaceLatitude(new Double(place.split("/")[1]));
                    ins.setPlaceLongitude(new Double(place.split("/")[2]));
                } else {
                    ins.setPlaceAddress(null);
                    ins.setPlaceLatitude(null);
                    ins.setPlaceLongitude(null);
                }


                if (form().bindFromRequest().get("time") != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    String time = form().bindFromRequest().get("time");
                    ins.setTimeStart(sdf.parse(time.split(",")[0]));
                    ins.setTimeEnd(sdf.parse(time.split(",")[1]));
                } else {
                    ins.setTimeStart(null);
                    ins.setTimeEnd(null);
                }

                for(InspirationCategory insCat : new InspirationCategoryDAO().getCategories(ins.getId()))
                    new InspirationCategoryDAO().delete(insCat);

                for (String key : form().bindFromRequest().data().keySet()) {
                    if (key.startsWith("category[")) {
                        InspirationCategory insCat = new InspirationCategory();
                        insCat.setCategoryId(form().bindFromRequest().data().get(key));
                        insCat.setInspirationId(ins.getId());
                        new InspirationCategoryDAO().save(insCat);
                    }
                }

                if (new InspirationDAO().save(ins)) {
//                    if (!ins.getCategoryId().equals(originalCategoryId)) {
//                        Logger.debug("deleting " + originalCategoryId + "," + ins.getInspirationId());
//                        new InspirationDAO().delete(originalCategoryId, ins.getInspirationId());
//                    }

                    new CategoryDAO().updateCategoryCountCache();
                    flash("success",
                            "Saved Inspiration with image " + ins.getImage()
                                    + ".");
                    //insForm = form(Inspiration.class);
                    return created(manage.render(insForm,
                            new CategoryDAO().allOptionsMap(),
                            new InspirationDAO().all()));
                } else
                    throw new Exception();
            } catch (Exception e) {
                flash("error", "Something went wrong during saving :(");
                Logger.error("inspiration saving went wrong", e);
                return internalServerError(manage.render(filledInsForm,
                        new CategoryDAO().allOptionsMap(),
                        new InspirationDAO().all()));
            }
        }
    }


    @Security.Authenticated(SecuredAdminUser.class)
    public static Result delete(String id) {
        try {
            AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                    ConfigFactory.load().getString("aws.accessKey"),
                    ConfigFactory.load().getString("aws.secretKey")));
            s3.deleteObject(S3_BUCKET_INSPIRATION_IMAGES, id);

            for (InspirationCategory insCat : new InspirationCategoryDAO().getCategories(id))
                new InspirationCategoryDAO().delete(insCat);

            if (new InspirationDAO().delete(id)) {
                flash("success", "Inspiration with id " + id + " deleted.");
                new CategoryDAO().updateCategoryCountCache();
                return ok(manage.render(insForm,
                        new CategoryDAO().allOptionsMap(),
                        new InspirationDAO().all()));
            } else
                throw new Exception();
        } catch (Exception e) {
            flash("error", "Something went wrong during deletion :(");
            return internalServerError(manage.render(insForm,
                    new CategoryDAO().allOptionsMap(),
                    new InspirationDAO().all()));
        }
    }
}
