package org.ednovo.gooru.core.api.model;

public enum PartyCategoryType {
	USER_META("user_meta"), USER_TAXONOMY("user_taxonomy"), USER_INFO("user_info"), ORGANIZATION_META("organization_meta"), ORGANIZATION_INFO("organization_info"), GROUP_INFO("group_info"), GROUP_META("group_meta");

	private String partyCategoryType;

	private PartyCategoryType(String partyCategoryType) {
		this.partyCategoryType = partyCategoryType;
	}

	public String getpartyCategoryType() {
		return this.partyCategoryType;
	}

}