package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class SkillsResult implements Serializable {

	
	private static final long serialVersionUID = -2781022142762993831L;
	
	private Integer codeId;
	
	private String name;

	
	public Integer getCodeId() {
		return codeId;
	}

	public String getName() {
		return name;
	}

	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
