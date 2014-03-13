package org.ednovo.gooru.core.api.model;

public class AnnotationType {
	
	private String name;
	private String description;
	
	public static enum Type{
		NOTE("note"),  QUOTE("quote"), RATING("rating"), SUBSCRIPTION("subscription"), COMMENT("comment");
		
		private String type;
		
		Type(String type){
			this.type = type;
		}
		
		public String getType() {
			return this.type;
		}
	}
	
	@Override
	public String toString()
	{
		return getName() + "_id:";
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
