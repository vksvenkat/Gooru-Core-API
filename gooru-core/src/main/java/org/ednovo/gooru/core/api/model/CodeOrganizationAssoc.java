package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class CodeOrganizationAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6985059348737939795L;
	private Code code;
	private String organizationCode;
	private Boolean isFeatured;
	private Integer sequence;
	private String ideas;
	private String questions;
	private String performanceTasks;

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
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

	public String getIdeas() {
		return ideas;
	}

	public void setIdeas(String ideas) {
		this.ideas = ideas;
	}

	public String getQuestions() {
		return questions;
	}

	public void setQuestions(String questions) {
		this.questions = questions;
	}

	public String getPerformanceTasks() {
		return performanceTasks;
	}

	public void setPerformanceTasks(String performanceTasks) {
		this.performanceTasks = performanceTasks;
	}

}
