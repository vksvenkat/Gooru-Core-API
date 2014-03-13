package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import flexjson.JSON;

public class StorageAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3367313832274558338L;


	private Integer storageAccountId;
	private String accountName;
	private String domainName;
	private String typeName;
	private String accessKey;
	private String accessSecret;
	private Date createdOn;

	public static enum Type {

		NFS("NFS"), S3("S3");

		private String type;

		Type(String type) {
			this.type = type;
		}

		public String getType() {
			return this.type;
		}
	}

	public Integer getStorageAccountId() {
		return storageAccountId;
	}

	public void setStorageAccountId(Integer storageAccountId) {
		this.storageAccountId = storageAccountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@JSON(include = false)
	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	@JSON(include = false)
	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

}
