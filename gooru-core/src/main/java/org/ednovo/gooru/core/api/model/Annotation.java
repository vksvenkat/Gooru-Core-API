package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Annotation extends Content implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7077508597807668460L;
	/**
	 * 
	 */
	
	
	private AnnotationType annotationType;
	private Resource resource;
	private String anchor;
	private String freetext;
	
	
	public AnnotationType getAnnotationType() {
		return annotationType;
	}
	public void setAnnotationType(AnnotationType annotationType) {
		this.annotationType = annotationType;
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public String getAnchor() {
		return anchor;
	}
	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	public String getFreetext() {
		return freetext;
	}
	public void setFreetext(String freetext) {
		this.freetext = freetext;
	}
}
