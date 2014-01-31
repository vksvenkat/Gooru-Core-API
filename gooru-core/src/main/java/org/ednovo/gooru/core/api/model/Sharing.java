package org.ednovo.gooru.core.api.model;

public enum Sharing {	

	PUBLIC("public"),PRIVATE("private"), ANYONEWITHLINK("anyonewithlink");
	
	private String sharing;
	
	private Sharing(String sharing){
		this.sharing = sharing;
	}
	
	public String getSharing(){
		return this.sharing;
	}

}
