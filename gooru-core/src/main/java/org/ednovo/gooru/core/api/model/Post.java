package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.CustomTableValue;


public class Post extends Content implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2120041760899270990L;
	private String title;
	private String freeText;
	private CustomTableValue type;
	private String assocGooruOid;
	private String assocUserUid;
	private CustomTableValue target;
	private CustomTableValue status;
	

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String postTitle) {
		this.title = postTitle;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String postText) {
		this.freeText = postText;
	}

	public String getAssocGooruOid() {
		return assocGooruOid;
	}

	public void setAssocGooruOid(String assocGooruOid) {
		this.assocGooruOid = assocGooruOid;
	}

	public String getAssocUserUid() {
		return assocUserUid;
	}

	public void setAssocUserUid(String assocUserUid) {
		this.assocUserUid = assocUserUid;
	}

	public void setTarget(CustomTableValue target) {
		this.target = target;
	}

	public CustomTableValue getTarget() {
		return target;
	}

	public void setType(CustomTableValue postType) {
		this.type = postType;
	}

	public CustomTableValue getType() {
		return type;
	}

}
