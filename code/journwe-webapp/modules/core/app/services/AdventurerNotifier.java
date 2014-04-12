package services;

import models.adventure.Adventure;
import models.adventure.adventurer.Adventurer;
import models.dao.adventure.AdventureDAO;
import models.dao.adventure.AdventurerDAO;
import models.notifications.ENotificationTopics;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 30.07.13
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class AdventurerNotifier extends UserNotifier {

    public void notifyAdventurers(String advId, String message, String subject) {
        notifyAdventurers(new AdventurerDAO().all(advId), message, subject);
    }

    public void notifyAdventurers(String advId, ENotificationTopics topic, String message) {
        notifyAdventurers(new AdventurerDAO().all(advId), topic, message);
    }

    public void notifyAdventurers(String advId, ENotificationTopics topic, String message, String subject) {
        notifyAdventurers(new AdventurerDAO().all(advId), topic, message, subject);
    }



    public void notifyAdventurers(Adventure adventure, String message, String subject) {
        notifyAdventurers(adventure.getId(), ENotificationTopics.ADVENTURE, message, subject);
    }

    public void notifyAdventurers(Adventure adventure, ENotificationTopics topic, String message) {
        notifyAdventurers(adventure.getId(), topic, message);
    }

    public void notifyAdventurers(Adventure adventure, ENotificationTopics topic, String message, String subject) {
        notifyAdventurers(adventure.getId(), topic, message, subject);
    }



    public void notifyAdventurers(Collection<Adventurer> adventurers, String message) {
        for (Adventurer advr : adventurers)
            notifyAdventurer(advr, message);
    }

    public void notifyAdventurers(Collection<Adventurer> adventurers, String message, String subject) {
        for (Adventurer advr : adventurers)
            notifyAdventurer(advr, message, subject);
    }

    public void notifyAdventurers(Collection<Adventurer> adventurers, ENotificationTopics topic, String message) {
        for (Adventurer advr : adventurers)
            notifyAdventurer(advr, topic, message);
    }

    public void notifyAdventurers(Collection<Adventurer> adventurers, ENotificationTopics topic, String message, String subject) {
        for (Adventurer advr : adventurers)
            notifyAdventurer(advr, topic, message, subject);
    }


    public void notifyAdventurer(Adventurer adventurer, String message) {
        notifyAdventurer(adventurer, message, null);
    }

    public void notifyAdventurer(Adventurer adventurer, String message, String subject) {
        notifyAdventurer(adventurer, ENotificationTopics.ADVENTURE, message, subject);
    }

    public void notifyAdventurer(Adventurer adventurer, ENotificationTopics topic, String message) {
        notifyAdventurer(adventurer, topic, message, null);
    }

    public void notifyAdventurer(Adventurer adventurer, ENotificationTopics topic, String message, String subject) {
        Adventure adventure = new AdventureDAO().get(adventurer.getAdventureId());
        super.notifyUser(adventurer.getUserId(), topic, adventure.getId(), message, (subject != null ? subject + " in " : "") + "Adventure \"" + adventure.getName() + "\"");
    }
}
