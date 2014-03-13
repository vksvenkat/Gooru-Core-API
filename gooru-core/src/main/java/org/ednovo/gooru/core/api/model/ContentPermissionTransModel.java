package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentPermissionTransModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5794456566304438840L;


	private String partyUid;

	public String getPartyUid() {
		return partyUid;
	}

	public void setPartyUid(String partyUid) {
		this.partyUid = partyUid;
	}

}
