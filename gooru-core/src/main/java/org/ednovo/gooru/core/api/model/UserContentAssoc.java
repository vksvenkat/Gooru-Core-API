package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;


public class UserContentAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5741682961442166533L;

	
	private User user;
	private Content content;
	private Integer relationshipId;

	private Date lastActiveDate;
	private String relationship;
	
	private User associatedBy;
	private Date associationDate;
	private String associatedType;
	
	public static enum RELATIONSHIP {

		PRACTICE("Practice", 1), CREATE("Create", 2), TEACH("Teach", 3), PUBLISH("Publish", 4), STUDY("Study", 5), SUBSCRIBE("Subscribe", 6), REPLY("Reply", 7), ASK("Ask", 8), UPDATE("Update", 9), QUOTE("Quote", 10), COLLABORATOR("Collaborator", 11);

		String name;
		int id;

		RELATIONSHIP(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Date getLastActiveDate() {
		return lastActiveDate;
	}

	public void setLastActiveDate(Date lastActiveDate) {
		this.lastActiveDate = lastActiveDate;
	}

	public Integer getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(Integer relationshipId) {
		if (relationshipId != null) {
			for (RELATIONSHIP relationship : RELATIONSHIP.values()) {
				if (relationshipId.equals(relationship.getId())) {
					this.relationshipId = relationshipId;
					if (this.relationship == null) {
						this.relationship = relationship.getName();
					}
					return;
				}
			}
		}
		this.relationshipId = null;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		if (relationship != null) {
			for (RELATIONSHIP relation : RELATIONSHIP.values()) {
				if (relationship.trim().equals(relation.getName())) {
					this.relationship = relationship;
					if (this.relationshipId == null) {
						this.relationshipId = relation.getId();
					}
					return;
				}
			}
		}
		this.relationship = relationship;
	}

	public void setContentRelationship(RELATIONSHIP relationship) {
		this.relationship = relationship.getName();
		this.relationshipId = relationship.getId();
	}

	public void setAssociatedBy(User associatedBy) {
		this.associatedBy = associatedBy;
	}

	public User getAssociatedBy() {
		return associatedBy;
	}

	public void setAssociationDate(Date associationDate) {
		this.associationDate = associationDate;
	}

	public Date getAssociationDate() {
		return associationDate;
	}

	public void setAssociatedType(String associatedType) {
		this.associatedType = associatedType;
	}

	public String getAssociatedType() {
		return associatedType;
	}
}
