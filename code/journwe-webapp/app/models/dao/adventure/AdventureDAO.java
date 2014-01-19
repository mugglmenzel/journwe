package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import models.adventure.Adventure;
import models.adventure.adventurer.Adventurer;
import models.adventure.comment.Comment;
import models.adventure.comment.CommentThread;
import models.adventure.file.JournweFile;
import models.adventure.place.PlacePreference;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimePreference;
import models.adventure.time.TimeOption;
import models.dao.common.CommonEntityDAO;
import models.dao.manytomany.ManyToManyListQuery;
import models.inspiration.Inspiration;

import java.util.ArrayList;
import java.util.List;

public class AdventureDAO extends CommonEntityDAO<Adventure> {

    private ManyToManyListQuery<Adventure, Inspiration> adventureToInspirationListQuery;

    public AdventureDAO() {
        super(Adventure.class);
        adventureToInspirationListQuery = new ManyToManyListQuery<Adventure, Inspiration>(Adventure.class, Inspiration.class);
    }

    public int adventureCountByUser(String userId) {
        return new AdventurerDAO().adventureCountByUser(userId);
    }

    // TODO this should probably be deprecated some time in the future
    public List<Adventure> all() {
        return pm.scan(clazz,
                new DynamoDBScanExpression());
    }

    public List<Adventure> listPublicAdventuresByInspiration(String inspirationId, String lastKey, int limit) {
        if(inspirationId==null)
            return new ArrayList<Adventure>();
        return adventureToInspirationListQuery.listM(inspirationId,lastKey,limit);
    }

    public void deleteFull(String advId) {
        for (Adventurer advr : new AdventurerDAO().all(advId))
            new AdventurerDAO().delete(advr);

        for (models.adventure.todo.Todo todo : new TodoDAO().all(advId))
            new TodoDAO().delete(todo);

        for (CommentThread ct : new CommentThreadDAO<Adventure>().all(advId)) {
            for (Comment c : new CommentDAO().getComments(ct.getThreadId()))
                new CommentDAO().delete(c);
            new CommentThreadDAO<Adventure>().delete(ct);
        }

        for (PlaceOption po : new PlaceOptionDAO().all(advId)) {
            for (PlacePreference pap : new PlacePreferenceDAO().all(po.getOptionId()))
                new PlacePreferenceDAO().delete(pap);
            new PlaceOptionDAO().delete(po);
        }

        for (TimeOption to : new TimeOptionDAO().all(advId)) {
            for (TimePreference tap : new TimePreferenceDAO().all(to.getOptionId()))
                new TimePreferenceDAO().delete(tap);
            new TimeOptionDAO().delete(to);
        }

        for (JournweFile jf : new JournweFileDAO().all(advId))
            new JournweFileDAO().delete(jf);

        // keep shortname to tell visitors it has been deleted
        //new AdventureShortnameDAO().delete(new AdventureShortnameDAO().getShortname(advId));

        new AdventureDAO().delete(advId);
    }

}
