package org.ednovo.gooru.core.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity(name="organization")
public class Organization extends Party {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2903580370776228049L;


	@Column
	private String organizationCode;

	private StorageArea s3StorageArea;

	private StorageArea nfsStorageArea;
	
	private Organization parentOrganization;
	
	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public StorageArea getS3StorageArea() {
		return s3StorageArea;
	}

	public void setS3StorageArea(StorageArea s3StorageArea) {
		this.s3StorageArea = s3StorageArea;
	}

	public StorageArea getNfsStorageArea() {
		return nfsStorageArea;
	}

	public void setNfsStorageArea(StorageArea nfsStorageArea) {
		this.nfsStorageArea = nfsStorageArea;
	}

	public void setParentOrganization(Organization parentOrganization) {
		this.parentOrganization = parentOrganization;
	}

	public Organization getParentOrganization() {
		return parentOrganization;
	}
}
