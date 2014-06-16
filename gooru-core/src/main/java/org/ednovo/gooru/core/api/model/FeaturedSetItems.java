package org.ednovo.gooru.core.api.model;

import java.io.Serializable;


public class FeaturedSetItems implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3181561815827806472L;
	/**
	 * 
	 */

	
	private Integer featuredSetItemId;
	private Content content;
	private Content parentContent;
	private Code code;
	private Integer sequence;
	private FeaturedSet featuredSet;

	public Integer getFeaturedSetItemId() {
		return featuredSetItemId;
	}

	public void setFeaturedSetItemId(Integer featuredSetItemId) {
		this.featuredSetItemId = featuredSetItemId;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Content getParentContent() {
		return parentContent;
	}

	public void setParentContent(Content parentContent) {
		this.parentContent = parentContent;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public FeaturedSet getFeaturedSet() {
		return featuredSet;
	}

	public void setFeaturedSet(FeaturedSet featuredSet) {
		this.featuredSet = featuredSet;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public Code getCode() {
		return code;
	}



}