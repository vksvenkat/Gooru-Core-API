package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

public class Party implements Serializable, IndexableEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4148011539334354199L;

	
	@Column
	private String partyUid;
	
	@Column
	private String partyName;
	
	@Column
	private String partyType;
	
	@Column
	private Date createdOn;
	
	@Column
	private Date lastModifiedOn;
	
	@Column
	private String userUid;
	
	@Column
	private String lastModifiedUserUid;
	
	private String displayName;
	
	private Boolean isPartner;
	
	private String organizationUid;
	
	public static enum TYPE {

		NETWORK("network"), ORGANIZATION("organization"), USER("user"), GROUP("group");

		private String name;

		TYPE(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(Date lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getPartyType() {
		return partyType;
	}

	public void setPartyType(String partyType) {
		this.partyType = partyType;
	}

	public String getLastModifiedUserUid() {
		return lastModifiedUserUid;
	}

	public void setLastModifiedUserUid(String lastModifiedUserUid) {
		this.lastModifiedUserUid = lastModifiedUserUid;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getPartyUid() {
		return partyUid;
	}

	public void setPartyUid(String partyUid) {
		this.partyUid = partyUid;
	}

	@Override
	public String getEntryId() {
		return partyUid;
	}

	public String getOrganizationUid() {
		return organizationUid;
	}

	public void setOrganizationUid(String organizationUid) {
		this.organizationUid = organizationUid;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setIsPartner(Boolean isPartner) {
		this.isPartner = isPartner;
	}

	public Boolean getIsPartner() {
		return isPartner;
	}

}