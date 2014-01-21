package controllers.api.json;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.typesafe.config.ConfigFactory;
import models.adventure.email.Message;
import models.auth.SecuredUser;
import models.dao.adventure.AdventureEmailMessageDAO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;

import static play.data.Form.form;

public class AdventureEmailController extends Controller {

    private static BasicAWSCredentials credentials = new BasicAWSCredentials(
            ConfigFactory.load().getString("aws.accessKey"), ConfigFactory
            .load().getString("aws.secretKey"));



    @Security.Authenticated(SecuredUser.class)
    public static Result listEmails(String adventureId) {
        activate(adventureId);
        List<Message> messages = new AdventureEmailMessageDAO().all(adventureId);
//        for(JournweFile file : files) {
//            Long newExpirationTimeInMillis = new Long(DateTime.now().getMillis()+EXPIRATION_TIME_IN_SECONDS);
//            String s3ObjectKey = generateS3ObjectKey(adventureId, file.getFileName());
//            String presignedUrl = s3.generatePresignedUrl(S3_BUCKET,
//                    s3ObjectKey, new Date(newExpirationTimeInMillis)).toString();
//            file.setUrl(presignedUrl);
//        }
        return ok(Json.toJson(messages));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result activateEmails(String advId) {
        activate(advId);
        return ok();
    }


    private static void activate(String advId) {
        AmazonSQS sqs = new AmazonSQSClient(credentials);
        sqs.sendMessage(new SendMessageRequest().withQueueUrl("journwe-email-bond").withMessageBody(advId));
        AmazonSNS sns = new AmazonSNSClient(credentials);
        sns.publish(new PublishRequest().withTopicArn("arn:aws:sns:us-east-1:561785394163:journwe-email-bond").withSubject("Wake Up, Bond!").withMessage("New Email Addresses!"));
    }
}
