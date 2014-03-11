/*******************************************************************************
 * CustomField.java
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

import org.ednovo.gooru.core.api.model.OrganizationModel;

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
