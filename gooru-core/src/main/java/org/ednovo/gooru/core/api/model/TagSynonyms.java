package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;


public class TagSynonyms implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6715445251063919884L;

	private Integer tagSynonymsId;
	
	private Date createdOn;
	
	private Date approvalOn;
	
	private String targetTagName;
	
	private User creator;
	
	private String  tagContentGooruOid;
	
	private User approver;
	
	private CustomTableValue status;

	public Integer getTagSynonymsId() {
		return tagSynonymsId;
	}

	public void setTagSynonymsId(Integer tagSynonymsId) {
		this.tagSynonymsId = tagSynonymsId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getApprovalOn() {
		return approvalOn;
	}

	public void setApprovalOn(Date approvalOn) {
		this.approvalOn = approvalOn;
	}

	public String getTargetTagName() {
		return targetTagName;
	}

	public void setTargetTagName(String targetTagName) {
		this.targetTagName = targetTagName;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public void setTagContentGooruOid(String tagContentGooruOid) {
		this.tagContentGooruOid = tagContentGooruOid;
	}

	public String getTagContentGooruOid() {
		return tagContentGooruOid;
	}

}
