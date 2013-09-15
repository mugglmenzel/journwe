package models.dao;

import com.amazonaws.services.s3.AmazonS3Client;
import models.adventure.file.JournweFile;
import models.dao.common.AdventureComponentDAO;

import java.util.List;

public class JournweFileDAO extends AdventureComponentDAO<JournweFile> {

    public JournweFileDAO() {
        super(JournweFile.class);
    }

    public void deleteFull() {
        //AmazonS3Client s3 = new AmazonS3Client(credentials);
        //s3.deleteObject(S3_BUCKET, advId + "/" + journweFile.getFileName());
    }

}
