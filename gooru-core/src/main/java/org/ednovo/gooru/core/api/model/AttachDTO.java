package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class AttachDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4444317032822963895L;


	private String id;

	private String type;
	
	private String  filename;
	
	private String mediaFilename; 

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setMediaFilename(String mediaFilename) {
		this.mediaFilename = mediaFilename;
	}

	public String getMediaFilename() {
		return mediaFilename;
	}

}
