package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "license")
public class License implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5665754531112851266L;
	public static final String OER = "OER";
	public static final String COMMERCIAL = "Commercial";
	public static final String OTHER = "Other";

	@Column
	private String name;
	
	@Column
	private String code;
	
	@Column
	private String icon;
	
	@Column
	private String definition;
	
	@Column
	private String url;
	
	@Column
	private String tag;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

}
