package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class JobType implements Serializable {

	private static final long serialVersionUID = -6275994990965281074L;

	private String name;
	private String description;

	public static enum Type {
		PPTCONVERSION("ppt/pptx-conversion"), PDFCONVERSION("pdf-conversion");

		private String type;

		Type(String type) {
			this.type = type;
		}

		public String getType() {
			return this.type;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
