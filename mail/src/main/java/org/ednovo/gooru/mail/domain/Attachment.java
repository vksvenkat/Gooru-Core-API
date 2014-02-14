package org.ednovo.gooru.mail.domain;

import java.io.Serializable;

public class Attachment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -302325258472260674L;
	
	private String url;
	
	private String fileName;

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

}
