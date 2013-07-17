package models.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.adventure.file.JournweFile;
import models.dao.common.CommonRangeEntityDAO;

import java.util.ArrayList;
import java.util.List;

public class JournweFileDAO extends CommonRangeEntityDAO<JournweFile> {

    public JournweFileDAO() {
        super(JournweFile.class);
    }
    
//	/**
//	 * Get the list of comments that belong to the thread, sorted by timestamp.
//	 */
//	public List<JournweFile> getFiles(final String adventureId) {
//        DynamoDBScanExpression scan = new DynamoDBScanExpression();
//        scan.addFilterCondition("adventureId", new Condition().withAttributeValueList(new AttributeValue(adventureId)).withComparisonOperator(ComparisonOperator.EQ));
//        PaginatedScanList<JournweFile> scanResult = pm.scan(JournweFile.class, scan);
//        return scanResult;
//	}

    public List<JournweFile> getFiles(final String adventureId) {
        JournweFile journweFileKey = new JournweFile();
        journweFileKey.setAdventureId(adventureId);
        // Hash key = adventure id
        DynamoDBQueryExpression<JournweFile> qe = new DynamoDBQueryExpression<JournweFile>().withHashKeyValues(journweFileKey);
        PaginatedQueryList<JournweFile> result = pm.query(JournweFile.class, qe);
        if(result != null)	{
            // return the results
            return result;
        } else {
            // ... else: return an empty list
            return new ArrayList<JournweFile>();
        }
    }
	
}
