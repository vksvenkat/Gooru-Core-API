package org.ednovo.gooru.core.api.model;

import org.ednovo.gooru.core.api.model.OrganizationModel;

public class ResponseFieldSet extends OrganizationModel  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3295699772461148222L;
	/**
	 * 
	 */
	
	private String fieldSetId;
	private String fieldSet;
	public String getFieldSetId() {
		return fieldSetId;
	}
	public void setFieldSetId(String fieldSetId) {
		this.fieldSetId = fieldSetId;
	}
	public String getFieldSet() {
		return fieldSet;
	}
	public void setFieldSet(String fieldSet) {
		this.fieldSet = fieldSet;
	}

	private String gooruUId;
	
	public String getGooruUId() {
		return gooruUId;
	}
	public void setGooruUId(String gooruUId) {
		this.gooruUId = gooruUId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
