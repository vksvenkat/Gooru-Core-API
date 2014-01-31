package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class RoleEntityOperation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1495650958924283197L;


	@JsonBackReference
	private UserRole userRole;
	private EntityOperation entityOperation;

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public EntityOperation getEntityOperation() {
		return entityOperation;
	}

	public void setEntityOperation(EntityOperation entityOperation) {
		this.entityOperation = entityOperation;
	}

}
