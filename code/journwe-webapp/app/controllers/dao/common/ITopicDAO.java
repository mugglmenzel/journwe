package controllers.dao.common;

import java.util.Iterator;

import models.CommentThread;

public interface ITopicDAO<T> {

	/**
	 * Get the comment threads that are associated with this topic. It might
	 * make sense to restrict this to one thread per topic in the user
	 * interface. For example, create new thread if no thread exists for this
	 * topic, yet. Otherwise, don't let users create a thread.
	 */
	public Iterator<CommentThread> getThreads(String adventureId, String topicId);

	// /**
	// * The topic is identified by a unique id, e.g., the checklist id, or the
	// id
	// * of any other object that is being commented.
	// */
	// public String getTopicId();
	//
	// public void setTopicId(String topicId);

}
