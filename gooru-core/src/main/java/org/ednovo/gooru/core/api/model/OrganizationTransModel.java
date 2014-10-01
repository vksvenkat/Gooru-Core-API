package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class OrganizationTransModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6216032474734214537L;


	private String organizationCode;

	private String organizationName;

	private String organizationUid;
	
	private String id;
	
	private CustomTableValue type;
	
	private String name;
	
	private String parentId;

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationUid() {
		return organizationUid;
	}

	public void setOrganizationUid(String organizationUid) {
		this.organizationUid = organizationUid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CustomTableValue getType() {
		return type;
	}

	public void setType(CustomTableValue type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
