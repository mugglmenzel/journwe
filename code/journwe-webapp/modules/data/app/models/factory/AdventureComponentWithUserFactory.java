package models.factory;

import models.adventure.IAdventureComponentWithUser;

public class AdventureComponentWithUserFactory<T extends IAdventureComponentWithUser> {

        public static <T extends IAdventureComponentWithUser> T newHasUser(Class<T> clazz) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;
        }
}
