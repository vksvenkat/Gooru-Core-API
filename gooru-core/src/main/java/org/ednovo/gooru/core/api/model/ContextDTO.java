package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContextDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -280214487667243922L;


	private String collectionGooruId;

	private String resourceGooruId;
	
	private String contentItemId;
	
	private String parentItemId;
	
	private String eventName;

	public void setCollectionGooruId(String collectionGooruId) {
		this.collectionGooruId = collectionGooruId;
	}

	public String getCollectionGooruId() {
		return collectionGooruId;
	}

	public void setResourceGooruId(String resourceGooruId) {
		this.resourceGooruId = resourceGooruId;
	}

	public String getResourceGooruId() {
		return resourceGooruId;
	}

	public String getContentItemId() {
		return contentItemId;
	}

	public void setContentItemId(String contentItemId) {
		this.contentItemId = contentItemId;
	}

	public String getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(String parentItemId) {
		this.parentItemId = parentItemId;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventName() {
		return eventName;
	}


}
