package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.typesafe.config.ConfigFactory;
import models.adventure.file.JournweFile;
import models.auth.SecuredBetaUser;
import models.authorization.AuthorizationMessage;
import models.authorization.JournweAuthorization;
import models.dao.JournweFileDAO;
import org.joda.time.DateTime;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

public class AdventureFileController extends Controller {

    public static final String S3_BUCKET = "journwe-files";
    public static final String DEFAULT_STORAGE_PROVIDER = "https://s3.amazonaws.com/" + S3_BUCKET;
    // expiration time = 24h
    public static final Long EXPIRATION_TIME_IN_SECONDS = 86400L;


    protected static Form<JournweFile> fileForm = form(JournweFile.class);

    private static BasicAWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"), ConfigFactory
            .load().getString("aws.secretKey"));
    private static AmazonS3Client s3 = new AmazonS3Client(credentials);


    @Security.Authenticated(SecuredBetaUser.class)
    public static Result uploadFile(String advId) {
        try {
            if (!JournweAuthorization.canUploadFiles(advId))
                return AuthorizationMessage.notAuthorizedResponse();
            AuthUser usr = PlayAuthenticate.getUser(Http.Context.current());
            if (usr == null)
                throw new Exception("File upload failed because user is null.");
            Form<JournweFile> filledFileForm = fileForm.bindFromRequest();
            if (fileForm.hasErrors()) {
                flash("error", "Sorry. Could not complete request because the form contains errors.");
                return badRequest();
            }

            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart uploadFile = body.getFile("uploadFile");
            if(uploadFile == null) return badRequest();

            File file = uploadFile.getFile();

            JournweFile journweFile = filledFileForm.get();
            journweFile.setAdventureId(advId);
            journweFile.setStorageProvider(DEFAULT_STORAGE_PROVIDER);
            journweFile.setUserId(usr.getId());
            String fileName = journweFile.getFileName();
            String s3ObjectKey = generateS3ObjectKey(advId, fileName);
            String url = DEFAULT_STORAGE_PROVIDER + "/" + s3ObjectKey;
            journweFile.setUrl(url);
            if (!new JournweFileDAO().save(journweFile))
                throw new Exception("Saving JournweFile in DynamoDB failed!");
            // Upload files to S3 asynchronously
            TransferManager tx = new TransferManager(credentials);
            Upload upload = tx.upload(S3_BUCKET, s3ObjectKey, file);
            s3.setObjectAcl(S3_BUCKET, s3ObjectKey, CannedAccessControlList.PublicRead);
            flash("success", "Your files is uploading now and can be downloaded, soon...");
            return ok(Json.toJson(journweFile));
        } catch (Exception e) {
            Logger.error("Failed uploading!", e);
            flash("error",
                    "File upload has failed. Sorry. Please try again later.");
            return internalServerError();
        }
    }



    @Security.Authenticated(SecuredBetaUser.class)
    public static Result listFiles(String adventureId) {
        if (!JournweAuthorization.canViewAndDownloadFiles(adventureId))
            return AuthorizationMessage.notAuthorizedResponse();
        List<JournweFile> files = new JournweFileDAO().all(adventureId);
//        for(JournweFile file : files) {
//            Long newExpirationTimeInMillis = new Long(DateTime.now().getMillis()+EXPIRATION_TIME_IN_SECONDS);
//            String s3ObjectKey = generateS3ObjectKey(adventureId, file.getFileName());
//            String presignedUrl = s3.generatePresignedUrl(S3_BUCKET,
//                    s3ObjectKey, new Date(newExpirationTimeInMillis)).toString();
//            file.setUrl(presignedUrl);
//        }
        return ok(Json.toJson(files));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result deleteFile(String advId, String fileId) {
        if (!JournweAuthorization.canDeleteFiles(advId))
            return AuthorizationMessage.notAuthorizedResponse();
        JournweFile journweFile = new JournweFileDAO().get(advId, fileId);

        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.deleteObject(S3_BUCKET, advId + "/" + journweFile.getFileName());

        new JournweFileDAO().delete(journweFile);


        return ok();
    }

    /**
     * Helper method.
     *
     * @param advId
     * @param fileName
     * @return
     */
    private static String generateS3ObjectKey(String advId, String fileName) {
        return advId + "/" + fileName;
    }

}
