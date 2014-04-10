package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("userRole")
public class UserRole extends OrganizationModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5564110791867719163L;
	private Short roleId;
	private String name;
	private String description;
	private Set<RoleEntityOperation> roleOperations;
	
	public static final Short ROLE_TEACHER = 1;
	public static final Short ROLE_STUDENT = 2;
	public static final Short ROLE_CONTENT_ADMIN = 3;
	public static final Short ROLE_ANONYMOUS = 4;
	public static final Short ROLE_AUTHENTICATED = 5;
	public static final Short ROLE_PUBLISHER = 6;
	public static final Short SUPER_ADMIN=7;

	public static enum UserRoleType{
		TEACHER("Teacher"),
		STUDENT("Student"),
		CONTENT_ADMIN("Content_Admin"),
		ANONYMOUS("ANONYMOUS"),
		AUTHENTICATED_USER("User"),
		OTHER("other"),
		PUBLISHER("Publisher"),
		SUPER_ADMIN("superadmin");
		private String type;
		UserRoleType(String type){
			this.type=type;
		}

		public String getType() {
			return type;
		}
		
	}
	
	public UserRole() {
		this.roleOperations = new HashSet<RoleEntityOperation>();
	}
		

	public Short getRoleId() {
		return roleId;
	}
	public void setRoleId(Short roleId) {
		this.roleId = roleId;
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
	public Set<RoleEntityOperation> getRoleOperations() {
		return roleOperations;
	}
	public void setRoleOperations(Set<RoleEntityOperation> roleOperations) {
		this.roleOperations = roleOperations;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserRole other = (UserRole) obj;
		if (roleId == null) {
			if (other.roleId != null) {
				return false;
			}
		} else if (!roleId.equals(other.roleId)) {
			return false;
		}
		return true;
	}

}