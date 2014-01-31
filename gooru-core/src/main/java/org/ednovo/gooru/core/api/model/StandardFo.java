package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class StandardFo implements Serializable {
	private static final long serialVersionUID = 4104089852580245018L;
	private Integer codeId;
	private String code;
	private String description;

	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}

	public Integer getCodeId() {
		return codeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
