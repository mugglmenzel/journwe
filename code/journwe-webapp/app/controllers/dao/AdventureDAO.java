package controllers.dao;

import controllers.dao.common.CommonChildDAO;
import models.Adventure;

public class AdventureDAO extends CommonChildDAO<Adventure> {

	@Override
	public Adventure get(String parentId, String id) {
		return pm.load(Adventure.class, parentId, id);
	}

}
