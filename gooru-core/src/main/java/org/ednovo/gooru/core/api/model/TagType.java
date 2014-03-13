package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class TagType implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -2366246787255555315L;
	private String name;
	private String description;
	
	public static enum Type{
			EXCITING("exciting"), QUESTION("question"), IMPORTANT("important"),
			USER("user"), SYSTEM("system"), VOCABULARY("vocabulary");
		
			private String type;
		
		Type(String type){
			this.type = type;
		}
		
		public String getType() {
			return this.type;
		}
	}
	
	
	
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
