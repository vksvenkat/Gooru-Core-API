/**
 * 
 */
package org.ednovo.gooru.core.api.model;

/**
 * @author parthi
 * 
 */
public enum PartyType {

	USER("user"), ORGANIZATION("organization"), USERGROUP("userGroup"), NETWORK("network");

	private String type;

	/**
	 * 
	 */
	PartyType(String type) {
		setType(type);
	}

	public String getType() {
		return type;
	}

	private void setType(String type) {
		this.type = type;
	}

}
