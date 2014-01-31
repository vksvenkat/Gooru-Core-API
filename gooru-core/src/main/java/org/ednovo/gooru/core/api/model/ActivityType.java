package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ActivityType implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6971278235495376178L;

	private String name;
	private String description;
	
	public static enum Type{
		
		CLASSPLANCREATE("classplan.create"),  CLASSPLANCOPY("classplan.copy"), RESOURCEADD("resource.add"), QUOTECREATE("quote.create"), CLASSPLANTEACH("classplan.teach"), CLASSBOOKCREATE("classbook.create"), CLASSBOOKCOPY("classbook.copy"), CLASSBOOKSTUDY("classbook.study");
		
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
