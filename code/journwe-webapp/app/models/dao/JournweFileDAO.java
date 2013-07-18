package models.dao;

import models.adventure.file.JournweFile;
import models.dao.common.AdventureComponentDAO;

import java.util.List;

public class JournweFileDAO extends AdventureComponentDAO<JournweFile> {

    public JournweFileDAO() {
        super(JournweFile.class);
    }

//    public List<JournweFile> getFiles(final String adventureId) {
//        JournweFile journweFileKey = new JournweFile();
//        journweFileKey.setAdventureId(adventureId);
//        // Hash key = adventure id
//        DynamoDBQueryExpression<JournweFile> qe = new DynamoDBQueryExpression<JournweFile>().withHashKeyValues(journweFileKey);
//        PaginatedQueryList<JournweFile> result = pm.query(JournweFile.class, qe);
//        if(result != null)	{
//            // return the results
//            return result;
//        } else {
//            // ... else: return an empty list
//            return new ArrayList<JournweFile>();
//        }
//    }

    public List<JournweFile> getFiles(final String adventureId) {
          return all(adventureId);
    }
}
