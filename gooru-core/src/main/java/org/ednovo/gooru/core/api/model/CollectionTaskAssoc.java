package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.User;


public class CollectionTaskAssoc implements Serializable, Comparable<CollectionTaskAssoc>	{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2338401508105131127L;


	private String collectionTaskAssocUid;

	private Collection collection;
	
	private Task task;

	private Integer sequence;

	private Date associationDate;

	private User associatedBy;
	
	private List<String> collectionIds;

	public Integer getSequence() {
		return sequence;
	}

	public Date getAssociationDate() {
		return associationDate;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setAssociationDate(Date associationDate) {
		this.associationDate = associationDate;
	}


	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollectionTaskAssocUid(String collectionTaskAssocUid) {
		this.collectionTaskAssocUid = collectionTaskAssocUid;
	}

	public String getCollectionTaskAssocUid() {
		return collectionTaskAssocUid;
	}

	public void setAssociatedBy(User associatedBy) {
		this.associatedBy = associatedBy;
	}

	public User getAssociatedBy() {
		return associatedBy;
	}

	@Override
	public int compareTo(CollectionTaskAssoc collectionTaskAssoc) {
		if (collectionTaskAssoc != null && getSequence() != null && collectionTaskAssoc.getSequence() != null && !getCollectionTaskAssocUid().equals(collectionTaskAssoc.getCollectionTaskAssocUid())) {
			if (getSequence().equals(collectionTaskAssoc.getSequence())) {
				return 0;
			}
			return getSequence().compareTo(collectionTaskAssoc.getSequence());
		}
		return 0;
	}

	public void setCollectionIds(List<String> collectionIds) {
		this.collectionIds = collectionIds;
	}

	public List<String> getCollectionIds() {
		return collectionIds;
	}

	
}
