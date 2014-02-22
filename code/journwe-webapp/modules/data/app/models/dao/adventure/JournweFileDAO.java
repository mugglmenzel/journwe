package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.s3.AmazonS3Client;
import models.adventure.file.JournweFile;
import models.adventure.log.AdventureLogEntry;
import models.dao.common.AdventureComponentDAO;

import java.util.ArrayList;
import java.util.List;

public class JournweFileDAO extends AdventureComponentDAO<JournweFile> {

    public JournweFileDAO() {
        super(JournweFile.class);
    }


    public List<JournweFile> allNewest(String advId) {
        JournweFile key = new JournweFile();
        key.setAdventureId(advId);

        return pm.query(JournweFile.class, new DynamoDBQueryExpression<JournweFile>().withHashKeyValues(key).withIndexName("timestamp-index").withScanIndexForward(false));
    }


    public List<JournweFile> allImages(String advId) {
        JournweFile key = new JournweFile();
        key.setAdventureId(advId);

        String[] imageFileEndings = new String[]{"jpg", "jpeg", "png", "gif"};

        List<AttributeValue> fileEndings = new ArrayList<AttributeValue>();
        for(String ending : imageFileEndings) {
            fileEndings.add(new AttributeValue(ending.toLowerCase()));
            fileEndings.add(new AttributeValue(ending.toUpperCase()));
        }

        return pm.query(JournweFile.class, new DynamoDBQueryExpression<JournweFile>().withHashKeyValues(key).withRangeKeyCondition("fileName", new Condition().withComparisonOperator(ComparisonOperator.CONTAINS).withAttributeValueList(fileEndings)).withScanIndexForward(false));

    }

    public void deleteFull() {
        //AmazonS3Client s3 = new AmazonS3Client(credentials);
        //s3.deleteObject(S3_BUCKET, advId + "/" + journweFile.getFileName());
    }

}
