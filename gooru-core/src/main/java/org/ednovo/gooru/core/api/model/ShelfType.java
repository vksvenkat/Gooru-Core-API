package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ShelfType implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7338178108877073352L;

	public static enum Category {
		SYSTEM("system"),
		USER("user"),
		VOCABULARY("vocabulary");
		private String category;	
		Category(String category) {
			this.category = category;
		}
		public String getCategory() {
			return this.category;
		}
	}
	
	public static enum AddedType {
		ADDED("added"),
		SUBSCRIBED("subscribed");
		private String addedType;	
		AddedType(String addedType) {
			this.addedType = addedType;
		}
		public String getAddedType() {
			return this.addedType;
		}
	}
}
