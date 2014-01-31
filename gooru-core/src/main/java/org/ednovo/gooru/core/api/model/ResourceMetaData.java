package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class ResourceMetaData  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3517417646072932821L;

	private String metaKey;
	private String metaContent;
	private Integer resourceMetaId;
	@JsonBackReference
	private Resource resource;
	
	public String getMetaKey() {
		return metaKey;
	}
	public void setMetaKey(String metaKey) {
		this.metaKey = metaKey;
	}
	public String getMetaContent() {
		return metaContent;
	}
	public void setMetaContent(String metaContent) {
		this.metaContent = metaContent;
	}
	public Integer getResourceMetaId() {
		return resourceMetaId;
	}
	public void setResourceMetaId(Integer resourceMetaId) {
		this.resourceMetaId = resourceMetaId;
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
