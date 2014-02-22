import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;
import play.Application;
import play.GlobalSettings;
import play.mvc.Call;

public class Global extends GlobalSettings {

    public void onStart(final Application app) {
        PlayAuthenticate.setResolver(new Resolver() {

            @Override
            public Call login() {
                // Your login page
                return controllers.core.html.routes.ApplicationController.index();
            }

            @Override
            public Call afterAuth() {
                // The user will be redirected to this page after authentication
                // if no original URL was saved
                return controllers.core.html.routes.ApplicationController.index();
            }

            @Override
            public Call afterLogout() {
                return controllers.core.html.routes.ApplicationController.index();
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


    }

}