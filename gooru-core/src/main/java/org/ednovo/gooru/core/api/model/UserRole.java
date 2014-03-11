/*******************************************************************************
 * UserRole.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
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
	
	public static enum UserRoleType{
		TEACHER("Teacher"),
		STUDENT("Student"),
		CONTENT_ADMIN("Content_Admin"),
		ANONYMOUS("ANONYMOUS"),
		AUTHENTICATED_USER("User"),
		OTHER("other"),
		PUBLISHER("Publisher");
		
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
