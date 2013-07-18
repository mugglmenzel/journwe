package models.dao;

import java.util.ArrayList;
import java.util.List;

import models.adventure.CommentThread;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

import models.dao.common.AdventureComponentDAO;
import models.dao.common.CommonRangeEntityDAO;

public class CommentThreadDAO<T>  extends AdventureComponentDAO<CommentThread> {
	
	public CommentThreadDAO() {
		super(CommentThread.class);
	}
}
