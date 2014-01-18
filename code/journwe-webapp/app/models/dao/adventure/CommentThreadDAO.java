package models.dao.adventure;

import models.adventure.comment.CommentThread;

import models.dao.common.AdventureComponentDAO;

public class CommentThreadDAO<T>  extends AdventureComponentDAO<CommentThread> {
	
	public CommentThreadDAO() {
		super(CommentThread.class);
	}
}
