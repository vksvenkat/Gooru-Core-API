package org.ednovo.gooru.core.api.model;


public class CustomField extends OrganizationModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3274809855566399630L;


	private String customFieldId;

	private String fieldName;

	private String fieldDisplayName;

	private String type;

	private double length;

	private String dataColumnName;

	private boolean addTosearch;

	private boolean isRequired;

	private String groupName;

	private String searchAliasName;
	
	private Integer addToSearchIndex;
	
	private Integer showInResponse;
	
	private Integer addToFilters;

	public String getCustomFieldId() {
		return customFieldId;
	}

	public void setCustomFieldId(String customFieldId) {
		this.customFieldId = customFieldId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDisplayName() {
		return fieldDisplayName;
	}

	public void setFieldDisplayName(String fieldDisplayName) {
		this.fieldDisplayName = fieldDisplayName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public String getDataColumnName() {
		return dataColumnName;
	}

	public void setDataColumnName(String dataColumnName) {
		this.dataColumnName = dataColumnName;
	}

	public boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public boolean getAddTosearch() {
		return addTosearch;
	}

	public void setAddTosearch(boolean addTosearch) {
		this.addTosearch = addTosearch;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSearchAliasName() {
		return searchAliasName;
	}

	public void setSearchAliasName(String searchAliasName) {
		this.searchAliasName = searchAliasName;
	}

	public Integer getShowInResponse() {
		return showInResponse;
	}

	public void setShowInResponse(Integer showInResponse) {
		this.showInResponse = showInResponse;
	}

	public Integer getAddToFilters() {
		return addToFilters;
	}

	public void setAddToFilters(Integer addToFilters) {
		this.addToFilters = addToFilters;
	}

	public Integer getAddToSearchIndex() {
		return addToSearchIndex;
	}

	public void setAddToSearchIndex(Integer addToSearchIndex) {
		this.addToSearchIndex = addToSearchIndex;
	}
	
	
	
	

}
