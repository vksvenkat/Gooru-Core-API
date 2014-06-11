package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Quote extends Annotation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8154603807013779771L;

	private String grade;
	private String title;
	private String topic;
	private String contextAnchor;
	private String contextAnchorText;

	public String getContextAnchor() {
		return contextAnchor;
	}

	public void setContextAnchor(String contextAnchor) {
		this.contextAnchor = contextAnchor;
	}

	public String getContextAnchorText() {
		return contextAnchorText;
	}

	public void setContextAnchorText(String contextAnchorText) {
		this.contextAnchorText = contextAnchorText;
	}

	private Content context;
	private License license;
	private TagType tagType;

	public TagType getTagType() {
		return tagType;
	}

	public void setTagType(TagType tagType) {
		this.tagType = tagType;
	}

	@Override
	public String toString() {
		return "quote_id:";
	}

	public Content getContext() {
		return context;
	}

	public void setContext(Content resource) {
		this.context = resource;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License licenseName) {
		license = licenseName;
	}
}
