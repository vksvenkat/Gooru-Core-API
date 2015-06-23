package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentMeta implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -580878754450962248L;
	private Content content;
	private String metaData;

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
}
