package org.ednovo.gooru.core.api.model;

public class CodeUserAssoc extends OrganizationModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7660486516006954957L;
	private Code code;
	private User user;
	private String  organizationCode;
	private Integer isOwner;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setIsOwner(Integer isOwner) {
		this.isOwner = isOwner;
	}
	public Integer getIsOwner() {
		return isOwner;
	}
	public void setCode(Code code) {
		this.code = code;
	}
	public Code getCode() {
		return code;
	}
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	public String getOrganizationCode() {
		return organizationCode;
	}

}
