/**
 * 
 */
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Search Team
 * 
 */
public class ContentPermission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4006373887414837153L;


	private Content content;

	private Party party;

	private String permission;

	private Date expiryDate;

	private Date validFrom;

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
}
