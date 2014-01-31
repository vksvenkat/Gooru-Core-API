/**
 * 
 */
package org.ednovo.gooru.core.api.model;

/**
 * @author rajam
 * 
 */
public enum PermissionType {

	VIEW("view"), EDIT("edit"), ALL("all"), OWNER("owner");

	private String type;

	/**
	 * 
	 */
	PermissionType(String type) {
		setType(type);
	}

	public String getType() {
		return type;
	}

	private void setType(String type) {
		this.type = type;
	}

}
