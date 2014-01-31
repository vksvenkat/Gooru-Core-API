package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentType implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -2842315869655303870L;
	public static final String RESOURCE = "resource";
	public static final String ANNOTATION = "annotation";
	public static final String TASK = "task";
	public static final String TAG = "tag";
	public static final String POST = "post";
	
	
	private String name;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
