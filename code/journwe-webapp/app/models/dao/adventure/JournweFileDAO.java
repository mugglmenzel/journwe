package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.s3.AmazonS3Client;
import models.adventure.file.JournweFile;
import models.adventure.log.AdventureLogEntry;
import models.dao.common.AdventureComponentDAO;

import java.util.List;

public class JournweFileDAO extends AdventureComponentDAO<JournweFile> {

    public JournweFileDAO() {
        super(JournweFile.class);
    }


    public List<JournweFile> allNewest(String advId) {
        JournweFile key = new JournweFile();
        key.setAdventureId(advId);

        return pm.query(JournweFile.class, new DynamoDBQueryExpression<JournweFile>().withHashKeyValues(key).withIndexName("timestamp-index").withRangeKeyCondition("timestamp", new Condition().withComparisonOperator(ComparisonOperator.GT).withAttributeValueList(new AttributeValue().withN("0"))).withScanIndexForward(false));

    }

    public void deleteFull() {
        //AmazonS3Client s3 = new AmazonS3Client(credentials);
        //s3.deleteObject(S3_BUCKET, advId + "/" + journweFile.getFileName());
    }

}
