package org.ednovo.gooru.core.api.model;

import java.io.Serializable;


public class PartyCustomField implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5604344044829539779L;
	private String partyUid;
	private String category;
	private String optionalKey;
	private String optionalValue;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getOptionalKey() {
		return optionalKey;
	}
	public String getOptionalValue() {
		return optionalValue;
	}
	public void setOptionalKey(String optionalKey) {
		this.optionalKey = optionalKey;
	}
	public void setOptionalValue(String optionalValue) {
		this.optionalValue = optionalValue;
	}
	public void setPartyUid(String partyUid) {
		this.partyUid = partyUid;
	}
	public String getPartyUid() {
		return partyUid;
	}
	

}
