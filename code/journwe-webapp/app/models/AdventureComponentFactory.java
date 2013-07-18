package models;

import models.adventure.IAdventureComponent;

public class AdventureComponentFactory<T extends IAdventureComponent> {

        public static <T extends IAdventureComponent> T newAdventureComponent(Class<T> clazz) {
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
