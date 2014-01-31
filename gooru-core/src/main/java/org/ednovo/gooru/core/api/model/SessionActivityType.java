package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class SessionActivityType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4088686410439271140L;


	public static enum Status{
		OPEN("open"),
		ARCHIVE("archive");
		private String status;	
		Status(String status){
			this.status = status;
		}
		public String getStatus() {
			return this.status;
		}
	}
	
	public static enum ContentType {
		COLLECTION("collection"),
		QUIZ("quiz"),
		RESOURCE("resource");
		private String contentType;	
		ContentType(String contentType){
			this.contentType = contentType;
		}
		
		public String getContentType() {
			return this.contentType;
		}
	}
}
