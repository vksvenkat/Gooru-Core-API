package org.ednovo.gooru.core.api.model;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Content;


public class ContentClassification {
	private Long classificationId;
	
	private Content content;
	private Code code;

	public Long getClassificationId() {
		return classificationId;
	}
	public void setClassificationId(Long classificationId) {
		this.classificationId = classificationId;
	}
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}

	public Code getCode() {
		return code;
	}
	public void setCode(Code code) {
		this.code = code;
	}

}
