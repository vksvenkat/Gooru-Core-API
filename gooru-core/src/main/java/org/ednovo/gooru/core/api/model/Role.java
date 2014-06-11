package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Role extends OrganizationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2556971434187488245L;
	/**
	 * 
	 */

	public static final Integer ROLE_TEACHER = 1;
	public static final Integer ROLE_STUDENT = 2;
	public static final Integer ROLE_CONTENT_ADMIN = 3;
	public static final Integer ROLE_ANONYMOUS = 4;
	public static final Integer ROLE_AUTHENTICATED = 5;
	public static final Integer ROLE_PUBLISHER = 6;

	private Integer roleId;

	private String name;

	private String description;

	private Set<RoleEntityOperation> roleOperations;
	
	public Role() {
		this.roleOperations = new HashSet<RoleEntityOperation>();
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Set<RoleEntityOperation> getRoleOperations() {
		return roleOperations;
	}

	public void setRoleOperations(Set<RoleEntityOperation> roleOperations) {
		this.roleOperations = roleOperations;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}