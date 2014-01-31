package org.ednovo.gooru.core.api.model;

public enum ImportMode {

	PROTECT("protect"), UPDATE("update");
	
	private String importMode;
	
	private ImportMode(String importMode){
		this.importMode = importMode;
	}
	
	public String getImportMode(){
		return this.importMode;
	}
}
