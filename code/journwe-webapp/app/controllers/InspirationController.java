package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.typesafe.config.ConfigFactory;
import models.auth.SecuredBetaUser;
import models.dao.CategoryDAO;
import models.dao.InspirationDAO;
import models.Category;
import models.Inspiration;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.inspiration.get;
import views.html.inspiration.manage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

public class InspirationController extends Controller {

    private static final String S3_BUCKET_INSPIRATION_IMAGES = "journwe-inspiration-images";

    private static Form<Inspiration> insForm = form(Inspiration.class);

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result get(String catId, String id) {
        Inspiration ins = new InspirationDAO().get(catId, id);
        Category cat = new CategoryDAO().get(ins.getCategoryId());
        AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                ConfigFactory.load().getString("aws.accessKey"),
                ConfigFactory.load().getString("aws.secretKey")));
        List<String> images = new ArrayList<String>();
        for(S3ObjectSummary os : s3.listObjects(S3_BUCKET_INSPIRATION_IMAGES, id + "/").getObjectSummaries()){
            images.add(s3.getResourceUrl(S3_BUCKET_INSPIRATION_IMAGES,
                    os.getKey()));
        }

        return ok(get.render(ins, cat, images));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result create() {
        return ok(manage.render(insForm, new CategoryDAO().allOptionsMap(),
                new InspirationDAO().all()));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result edit(String catId, String id) {
        Form<Inspiration> editInsForm = insForm.fill(new InspirationDAO().get(catId, id));
        return ok(manage.render(editInsForm,
                new CategoryDAO().allOptionsMap(),
                new InspirationDAO().all()));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result save() {

        Form<Inspiration> filledInsForm = insForm.bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart image = body.getFile("image");

        if (filledInsForm.hasErrors()) {
            flash("error", "Please fill out the form correctly.");
            return badRequest(manage.render(filledInsForm,
                    new CategoryDAO().allOptionsMap(),
                    new InspirationDAO().all()));
        } else {
            Inspiration ins = filledInsForm.get();

            File file = image.getFile();

            try {
                if (ins.getInspirationId() == null && !new InspirationDAO().save(ins))
                    throw new Exception();


                Logger.info("got image files " + image.getFilename());
                if (image.getFilename() != null
                        && !"".equals(image.getFilename()) && file.length() > 0) {
                    AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                            ConfigFactory.load().getString("aws.accessKey"),
                            ConfigFactory.load().getString("aws.secretKey")));
                    s3.putObject(new PutObjectRequest(
                            S3_BUCKET_INSPIRATION_IMAGES, ins.getInspirationId() + "/title", file)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    ins.setImage(s3.getResourceUrl(S3_BUCKET_INSPIRATION_IMAGES,
                            ins.getInspirationId() + "/title"));
                }  else
                    ins.setImage(new InspirationDAO().get(ins.getCategoryId(), ins.getInspirationId()).getImage());



                if((ins.getPlaceAddress() == null && form().bindFromRequest().get("place") != null) ||(ins.getPlaceAddress() != null && !ins.getPlaceAddress().equals(form().bindFromRequest().get("place")))) {
                    String place = form().bindFromRequest().get("place");
                    Logger.debug("got place: " + place + ", saving: " + place.split("/")[0] + ", " + place.split("/")[1] + ", " + place.split("/")[2]);
                    ins.setPlaceAddress(place.split("/")[0]);
                    ins.setPlaceLatitude(new Double(place.split("/")[1]));
                    ins.setPlaceLongitude(new Double(place.split("/")[2]));
                }


                if (new InspirationDAO().save(ins)) {

                    flash("success",
                            "Saved Inspiration with image " + ins.getImage()
                                    + ".");
                    insForm = form(Inspiration.class);
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

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result delete(String catId, String id) {
        try {
            AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                    ConfigFactory.load().getString("aws.accessKey"),
                    ConfigFactory.load().getString("aws.secretKey")));
            s3.deleteObject(S3_BUCKET_INSPIRATION_IMAGES, id);
            if (new InspirationDAO().delete(catId, id)) {
                flash("success", "Inspiration with id " + id + " deleted.");
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
