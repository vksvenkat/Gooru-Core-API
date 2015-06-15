package org.ednovo.gooru.core.api.model;


public class UserGroup extends Party implements OrganizationWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1149866471278182564L;

	private String name;
	
	private String groupCode;

	private boolean activeFlag;

	private String userGroupType;

	private Organization organization;
	
	private int memberCount;
	
	
	@Override
	public Organization getOrganization() {
		return organization;
	}

	@Override
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public String getUserGroupType() {
		return userGroupType;
	}

	public void setUserGroupType(String userGroupType) {
		this.userGroupType = userGroupType;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}
			
}