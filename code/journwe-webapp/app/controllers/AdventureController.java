package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.feth.play.module.pa.PlayAuthenticate;
import com.typesafe.config.ConfigFactory;
import controllers.auth.SecuredAdminUser;
import controllers.dao.AdventureDAO;
import controllers.dao.AdventurerDAO;
import controllers.dao.InspirationDAO;
import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.create;
import views.html.adventure.get;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import static play.data.Form.form;

public class AdventureController extends Controller {

    private static final String S3_BUCKET_ADVENTURE_IMAGES = "journwe-adventure-images";

    private static Form<Adventure> advForm = form(Adventure.class);

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result get(String id) {
        Adventure adv = new AdventureDAO().get(id);
        return ok(get.render(adv, new InspirationDAO().get(adv.getInspirationId()), new AdventurerDAO().all(50, id)));
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result create() {
        return createFromInspiration(null);
    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result createFromInspiration(String insId) {
        Map<String, String> inspireOptions = new InspirationDAO()
                .allOptionsMap(50);
        Form<Adventure> advFilledForm = advForm;
        if (insId != null && !"".equals(insId)) {
            Adventure adv = new Adventure();
            adv.setInspirationId(insId);
            advFilledForm = advForm.fill(adv);
        } else advFilledForm = advForm.fill(new Adventure());
        return ok(create.render(advFilledForm, inspireOptions));

    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result save() {
        Form<Adventure> filledAdvForm = advForm.bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart image = body.getFile("image");

        if (filledAdvForm.hasErrors()) {
            return badRequest(create.render(filledAdvForm,
                    new InspirationDAO().allOptionsMap(50)));

        } else {
            Adventure adv = filledAdvForm.get();
            File file = image.getFile();

            try {
                if (!new AdventureDAO().save(adv))
                    throw new Exception();

                adv = new AdventureDAO().get(adv.getId());
                Inspiration ins = new InspirationDAO().get(adv.getInspirationId());

                Logger.info("got image file " + image.getFilename());

                if (image.getFilename() != null
                        && !"".equals(image.getFilename()) && file.length() > 0) {
                    AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                            ConfigFactory.load().getString("aws.accessKey"),
                            ConfigFactory.load().getString("aws.secretKey")));
                    s3.putObject(new PutObjectRequest(
                            S3_BUCKET_ADVENTURE_IMAGES, adv.getId(), file)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    adv.setImage(s3.getResourceUrl(S3_BUCKET_ADVENTURE_IMAGES,
                            adv.getId()));
                } else
                    adv.setImage(ins.getImage());


                if (new AdventureDAO().save(adv)) {

                    flash("success",
                            "Saved Adventure with image " + adv.getImage()
                                    + ".");
                    return ok(get.render(new AdventureDAO().get(adv.getId()), ins, new AdventurerDAO().all(50, adv.getId())));

                } else
                    throw new Exception();
            } catch (Exception e) {
                flash("error", "Something went wrong during saving :(");
                return internalServerError(create.render(filledAdvForm,
                        new InspirationDAO()
                                .allOptionsMap(50)));
            }
        }

    }

    @Security.Authenticated(SecuredAdminUser.class)
    public static Result participate(String advId) {
        Logger.info("u are user: " + PlayAuthenticate.getUser(Http.Context.current()).getId());
        User usr = User.findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventure adv = new AdventureDAO().get(advId);

        Iterator<Adventurer> advrs = new AdventurerDAO().findByAdventureId(advId);
        while (advrs.hasNext()) {
            if (usr.getProviderUserId() != null && usr.getProviderUserId().equals(advrs.next().getUserId()))
                return ok(get.render(adv, new InspirationDAO().get(adv.getInspirationId()), new AdventurerDAO().all(50, advId)));
        }


        Adventurer advr = new Adventurer();
        advr.setUserId(usr.getProviderUserId());
        advr.setAdventureId(advId);
        advr.setParticipationStatus(EAdventurerParticipation.GOING);
        new AdventurerDAO().save(advr);


        return ok(get.render(adv, new InspirationDAO().get(adv.getInspirationId()), new AdventurerDAO().all(50, advId)));
    }

}
