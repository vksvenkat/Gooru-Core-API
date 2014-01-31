package org.ednovo.gooru.core.api.model;

import org.ednovo.gooru.core.api.model.Code;

public class CodeWrapper {
	
	private Code code;
	private String titlesText;
	
	public Code getCode() {
		return code;
	}
	
	public void setCode(Code code) {
		this.code = code;
	}
	
	public String getTitlesText() {
		return titlesText;
	}
	
	public void setTitlesText(String titleText) {
		titlesText = titleText;
	}
}
