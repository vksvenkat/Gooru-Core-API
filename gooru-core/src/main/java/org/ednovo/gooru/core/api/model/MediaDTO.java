package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class MediaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3921784148327314532L;

	private String filename;
	
	private String imageURL;
	
	private Boolean resize =  true;
	
	private Integer width = 700;
	
	private Integer height = 525;

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Boolean getResize() {
		return resize;
	}

	public void setResize(Boolean resize) {
		this.resize = resize;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
	
}
