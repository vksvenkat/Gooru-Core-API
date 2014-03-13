/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "license")
public class LicenseCo {

	@Column
	private String name;

	@Column
	private String url;

	@Column
	private String code;

	@Column
	private String tag;

	@Column
	private String icon;

	@Column
	private String definition;

	public String getName() {
		return name;
	}

	public void setName(String license) {
		this.name = license;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String licenseUrl) {
		this.url = licenseUrl;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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
}