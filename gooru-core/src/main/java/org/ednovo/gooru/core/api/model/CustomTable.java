package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class CustomTable extends OrganizationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7545253432645705302L;

	private Integer CustomTableId;

	private String name;

	private String displayName;

	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCustomTableId(Integer customTableId) {
		CustomTableId = customTableId;
	}

	public Integer getCustomTableId() {
		return CustomTableId;
	}

}
