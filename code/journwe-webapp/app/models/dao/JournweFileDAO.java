package models.dao;

import models.adventure.file.JournweFile;
import models.dao.common.AdventureComponentDAO;

import java.util.List;

public class JournweFileDAO extends AdventureComponentDAO<JournweFile> {

    public JournweFileDAO() {
        super(JournweFile.class);
    }

}
