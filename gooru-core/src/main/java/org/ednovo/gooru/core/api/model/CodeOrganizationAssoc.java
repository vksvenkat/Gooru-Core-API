package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class CodeOrganizationAssoc  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6985059348737939795L;
	private Code code;
	private String organizationCode;
	private Boolean isFeatured;
	private Integer sequence;
	public Code getCode() {
		return code;
	}
	public void setCode(Code code) {
		this.code =code;
	}
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	public String getOrganizationCode() {
		return organizationCode;
	}
	public void setIsFeatured(Boolean isFeatured) {
		this.isFeatured = isFeatured;
	}
	public Boolean getIsFeatured() {
		return isFeatured;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Integer getSequence() {
		return sequence;
	}
	

}
