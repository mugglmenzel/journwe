import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;
import com.typesafe.config.ConfigFactory;
import controllers.api.json.AdventureFileController;
import controllers.html.AdventureController;
import models.adventure.Adventure;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.dao.category.CategoryDAO;
import models.notifications.ENotificationFrequency;
import models.notifications.helper.UserNotifier;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.libs.Akka;
import play.mvc.Call;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class Global extends GlobalSettings {

    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }

    public void onStart(final Application app) {

        PlayAuthenticate.setResolver(new PlayAuthenticate.Resolver() {

            @Override
            public Call login() {
                // Your login page
                return controllers.html.routes.IndexController.index();
            }

            @Override
            public Call afterAuth() {
                // The user will be redirected to this page after authentication
                // if no original URL was saved
                return controllers.html.routes.IndexController.index();
            }

            @Override
            public Call afterLogout() {
                return controllers.html.routes.IndexController.index();
            }

            @Override
            public Call auth(final String provider) {
                // You can provide your own authentication implementation,
                // however the default should be sufficient for most cases
                return com.feth.play.module.pa.controllers.routes.Authenticate
                        .authenticate(provider);
            }

            @Override
            public Call onException(final AuthException e) {
                if (e instanceof AccessDeniedException) {
                    return controllers.core.html.routes.ApplicationController
                            .oAuthDenied(((AccessDeniedException) e)
                                    .getProviderKey());
                }

                // more custom problem handling here...

                return super.onException(e);
            }

            @Override
            public Call askLink() {
                return controllers.core.html.routes.AccountController.askLink();
            }

            @Override
            public Call askMerge() {
                return controllers.core.html.routes.AccountController.askMerge();
            }
        });

        Akka.system().scheduler().schedule(Duration.create(20, TimeUnit.SECONDS), Duration.create(1, TimeUnit.DAYS), new Runnable() {
            @Override
            public void run() {
                Logger.debug("Checking for daily digests to send");
                new UserNotifier().notifyUsersDigest(ENotificationFrequency.DAILY, 23 * 60 * 60 * 1000);
            }
        }, Akka.system().dispatcher());


        Akka.system().scheduler().schedule(Duration.create(120, TimeUnit.SECONDS), Duration.create(7, TimeUnit.DAYS), new Runnable() {
            @Override
            public void run() {
                Logger.debug("Checking for weekly digests to send");
                new UserNotifier().notifyUsersDigest(ENotificationFrequency.WEEKLY, 6 * 24 * 60 * 60 * 1000);
            }
        }, Akka.system().dispatcher());


        Akka.system().scheduler().schedule(Duration.create(240, TimeUnit.SECONDS), Duration.create(1, TimeUnit.DAYS), new Runnable() {
            @Override
            public void run() {
                Logger.debug("Cleaning Adventures without Adventurers");
                for (Adventure adv : new AdventureDAO().all())
                    if (!(new AdventurerDAO().userCountByAdventure(adv.getId()) > 0)) {
                        new AdventureDAO().deleteFull(adv.getId());
                        //delete s3 objects
                        AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
                                ConfigFactory.load().getString("aws.accessKey"),
                                ConfigFactory.load().getString("aws.secretKey")));
                        for (S3ObjectSummary obj : s3.listObjects(AdventureController.S3_BUCKET_ADVENTURE_IMAGES, adv.getId()).getObjectSummaries())
                            s3.deleteObject(
                                    AdventureController.S3_BUCKET_ADVENTURE_IMAGES, obj.getKey());
                        for (S3ObjectSummary obj : s3.listObjects(AdventureFileController.S3_BUCKET, adv.getId()).getObjectSummaries())
                            s3.deleteObject(
                                    AdventureFileController.S3_BUCKET, obj.getKey());
                    }
            }
        }, Akka.system().dispatcher());


        Akka.system().scheduler().schedule(Duration.create(60, TimeUnit.SECONDS), Duration.create(1, TimeUnit.DAYS), new Runnable() {
            @Override
            public void run() {
                Logger.debug("Updating Category Count Cache");
                new CategoryDAO().updateCategoryCountCache();

            }
        }, Akka.system().dispatcher());


    }

}