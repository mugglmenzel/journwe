package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.typesafe.config.ConfigFactory;
import models.adventure.file.JournweFile;
import models.auth.SecuredBetaUser;
import models.dao.JournweFileDAO;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

import java.io.File;
import java.util.List;

import static play.data.Form.form;

public class AdventureFileController extends Controller {

    public static final String S3_BUCKET = "journwe-files";
    public static final String DEFAULT_STORAGE_PROVIDER="https://s3.amazonaws.com/"+S3_BUCKET;

    protected static Form<JournweFile> fileForm = form(JournweFile.class);

    private static BasicAWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"), ConfigFactory
            .load().getString("aws.secretKey"));

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result uploadFile(String advId) {
        try {
        AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
        if(usr==null)
            throw new Exception("File upload failed because user is null.");
        Form<JournweFile> filledFileForm = fileForm.bindFromRequest();
        if (fileForm.hasErrors()) {
            flash("error", "Sorry. Could not complete request because the form contains errors.");
            return badRequest();
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFile = body.getFile("uploadFile");
        File file = uploadFile.getFile();

        JournweFile journweFile = filledFileForm.get();
        journweFile.setAdventureId(advId);
        journweFile.setStorageProvider(DEFAULT_STORAGE_PROVIDER);
        journweFile.setUserId(usr.getId());
        String s3ObjectKey = advId+"/"+journweFile.getFileName();
        String url = DEFAULT_STORAGE_PROVIDER+"/"+s3ObjectKey;
        journweFile.setUrl(url);
        if(!new JournweFileDAO().save(journweFile))
            throw new Exception("Saving JournweFile in DynamoDB failed!");
        // Upload files to S3 asynchronously
        TransferManager tx = new TransferManager(credentials);
        Upload upload = tx.upload(S3_BUCKET, s3ObjectKey, file);
        flash("success", "Your files is uploading now and can be downloaded, soon...");
        return ok(Json.toJson(journweFile));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            flash("error",
                    "File upload has failed. Sorry. Please try again later.");
            return internalServerError();
        }
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result listFiles(String adventureId) {
        Logger.debug("Listing files from DynamoDB ...");
        List<JournweFile> files = new JournweFileDAO().getFiles(adventureId);
        for(JournweFile file : files)
            Logger.debug(file.getFileName());
        return ok(Json.toJson(files));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result deleteFile(String id, String tid) {

        new JournweFileDAO().delete(tid, id);

        return ok(); //TODO: Error handling
    }

}