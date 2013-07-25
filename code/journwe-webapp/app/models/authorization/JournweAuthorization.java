package models.authorization;

import com.feth.play.module.pa.PlayAuthenticate;
import models.adventure.AdventureAuthorization;
import models.adventure.EAuthorizationRole;
import models.dao.AdventureAuthorizationDAO;
import models.dao.UserDAO;
import models.user.User;
import play.mvc.Http;

public class JournweAuthorization {

    // ADVENTURE INFO
    public static boolean canEditAdventureImage(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        AbstractAuthorization authorization = getAuthorization(advId,usr);
        return (authorization!=null) ? authorization.canEditAdventureImage() : false;
    }

    public static boolean canViewAdventureImage(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        AbstractAuthorization authorization = getAuthorization(advId,usr);
        return (authorization!=null) ? authorization.canViewAdventureImage() : false;
    }
    public static boolean canEditAdventureTitle(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        AbstractAuthorization authorization = getAuthorization(advId,usr);
        return (authorization!=null) ? authorization.canEditAdventureTitle() : false;
    }
    public static boolean canViewAdventureTitle(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        AbstractAuthorization authorization = getAuthorization(advId,usr);
        return (authorization!=null) ? authorization.canViewAdventureTitle() : false;
    }
    public static boolean canEditAdventureComments(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        AbstractAuthorization authorization = getAuthorization(advId,usr);
        return (authorization!=null) ? authorization.canEditAdventureComments() : false;
    }
    public static boolean canViewAdventureComments(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        AbstractAuthorization authorization = getAuthorization(advId,usr);
        return (authorization!=null) ? authorization.canViewAdventureComments() : false;
    }
    public static boolean canEditAdventureDescription(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        AbstractAuthorization authorization = getAuthorization(advId,usr);
        return (authorization!=null) ? authorization.canEditAdventureDescription() : false;
    }
    public static boolean canViewAdventureDescription(final String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        AbstractAuthorization authorization = getAuthorization(advId,usr);
        return (authorization!=null) ? authorization.canViewAdventureDescription() : false;
    }

    // PLACE

    // TIME

    // TODOLIST

    // ...

    private static AbstractAuthorization getAuthorization(final String advId,final User usr) {
        try {
            // Load JournweAuthorization classname from database
            AdventureAuthorization model = new AdventureAuthorizationDAO().get(advId, usr.getId());
            if(model==null) return null;
            EAuthorizationRole authorizationRole = model.getAuthorizationRole();
            String className = authorizationRole.getClassName();
            Class clazz = Class.forName(className);
            Object obj = clazz.newInstance();
            if(obj instanceof AbstractAuthorization) {
                AbstractAuthorization authorization =  (AbstractAuthorization)obj;
                return authorization;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

}
