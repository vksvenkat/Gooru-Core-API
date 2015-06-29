package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentClassification implements Serializable {

	private static final long serialVersionUID = -6081534845201356296L;

	private Content content;
	private Code code;
	private Short typeId;

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

	public Short getTypeId() {
		return typeId;
	}

	public void setTypeId(Short typeId) {
		this.typeId = typeId;
	}

}
