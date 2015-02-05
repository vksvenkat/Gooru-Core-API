package org.ednovo.gooru.core.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.ednovo.gooru.core.cassandra.model.IsCassandraIndexable;


@Entity(name="organization")
public class Organization extends Party  implements IsCassandraIndexable {
	 
 	/**
 	 * 
 	 */
 	private static final long serialVersionUID = -2903580370776228049L;

  
	private static final String  INDEX_TYPE = "schooldistrict";

	@Column
	private String organizationCode;
	
	@Column
	private StorageArea s3StorageArea;
 
	@Column
	private StorageArea nfsStorageArea;
	
	@Column
	private Organization parentOrganization;
	
	@Column
	private String parentId;
	
	@Column
	private Province stateProvince;
    
	@Column
    private CustomTableValue type;

	public String getOrganizationCode() {
		return organizationCode;
	}

	public StorageArea getS3StorageArea() {
		return s3StorageArea;
	}

	public StorageArea getNfsStorageArea() {
		return nfsStorageArea;
	}

	public Organization getParentOrganization() {
		return parentOrganization;
	}

	public String getParentId() {
		return parentId;
	}

	public Province getStateProvince() {
		return stateProvince;
	}

	public CustomTableValue getType() {
		return type;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public void setS3StorageArea(StorageArea s3StorageArea) {
		this.s3StorageArea = s3StorageArea;
	}

	public void setNfsStorageArea(StorageArea nfsStorageArea) {
		this.nfsStorageArea = nfsStorageArea;
	}

	public void setParentOrganization(Organization parentOrganization) {
		this.parentOrganization = parentOrganization;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setStateProvince(Province stateProvince) {
		this.stateProvince = stateProvince;
	}

	public void setType(CustomTableValue type) {
		this.type = type;
	}

	@Override
	public String getIndexId() {
		return getPartyUid();
	}

	@Override
	public String getIndexType() {	
		return INDEX_TYPE;
	}
	
 }