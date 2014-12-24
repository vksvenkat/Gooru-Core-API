/**
 * 
 */
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author rajam
 * 
 */
public class PartyPermission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8005082868081913621L;

	private String PartyPermissionUid;
	
	private Party party;

	private Party permittedParty;

	private String permission;

	private Date expiryDate;

	private Date validFrom;

	public Party getPermittedParty() {
		return permittedParty;
	}

	public void setPermittedParty(Party permittedParty) {
		this.permittedParty = permittedParty;
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

	public String getPartyPermissionUid() {
		return PartyPermissionUid;
	}

	public void setPartyPermissionUid(String partyPermissionUid) {
		PartyPermissionUid = partyPermissionUid;
	}

}
