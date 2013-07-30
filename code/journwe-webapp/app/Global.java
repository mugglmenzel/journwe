import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;
import controllers.routes;
import models.dao.NotificationDAO;
import models.notifications.Notification;
import models.notifications.UserNotifier;
import models.user.User;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import play.mvc.Call;
import scala.concurrent.duration.Duration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Global extends GlobalSettings {

    public void onStart(final Application app) {
        PlayAuthenticate.setResolver(new Resolver() {

            @Override
            public Call login() {
                // Your login page
                return routes.ApplicationController.index();
            }

            @Override
            public Call afterAuth() {
                // The user will be redirected to this page after authentication
                // if no original URL was saved
                return routes.ApplicationController.index();
            }

            @Override
            public Call afterLogout() {
                return routes.ApplicationController.index();
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
                    return routes.ApplicationController
                            .oAuthDenied(((AccessDeniedException) e)
                                    .getProviderKey());
                }

                // more custom problem handling here...

                return super.onException(e);
            }

            @Override
            public Call askLink() {
                // We don't support moderated account linking in this sample.
                // See the play-authenticate-usage project for an example
                return null;
            }

            @Override
            public Call askMerge() {
                // We don't support moderated account merging in this sample.
                // See the play-authenticate-usage project for an example
                return null;
            }
        });


        Akka.system().scheduler().schedule(Duration.create(20, TimeUnit.SECONDS), Duration.create(1, TimeUnit.DAYS), new Runnable() {
            @Override
            public void run() {
                Logger.debug("Checking for daily digests to send");
                for (User user : new NotificationDAO().getDailyDigestUsers()) {
                    if(new Date().getTime() > user.getLastDigest().getTime() + 23*60*60*1000)
                        new UserNotifier().notifyUserViaEmailDigest(new NotificationDAO().all(user.getId()), "Daily");
                }
            }
        }, Akka.system().dispatcher());


        Akka.system().scheduler().schedule(Duration.create(120, TimeUnit.SECONDS), Duration.create(7, TimeUnit.DAYS), new Runnable() {
            @Override
            public void run() {
                Logger.debug("Checking for weekly digests to send");
                for (User user : new NotificationDAO().getDailyDigestUsers()) {
                    if(new Date().getTime() > user.getLastDigest().getTime() + 6*24*60*60*1000)
                        new UserNotifier().notifyUserViaEmailDigest(new NotificationDAO().all(user.getId()), "Weekly");
                }
            }
        }, Akka.system().dispatcher());

    }

}