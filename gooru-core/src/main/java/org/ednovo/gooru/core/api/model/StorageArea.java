package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import flexjson.JSON;

public class StorageArea implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1528832230020997454L;

	
	private Integer storageAreaId;
	private transient StorageAccount storageAccount;
	private String areaName;
	private String areaPath;
	private String cdnPath;
	private Date createdOn;
	private String internalPath;
	private String cdnDirectPath;
	private String s3Path;

	@JSON(include = false)
	public Integer getStorageAreaId() {
		return storageAreaId;
	}

	public void setStorageAreaId(Integer storageAreaId) {
		this.storageAreaId = storageAreaId;
	}

	@JSON(include = false)
	public StorageAccount getStorageAccount() {
		return storageAccount;
	}

	public void setStorageAccount(StorageAccount storageAccount) {
		this.storageAccount = storageAccount;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getCdnPath() {
		return cdnPath;
	}

	public void setCdnPath(String cdnPath) {
		this.cdnPath = cdnPath;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getAreaPath() {
		return areaPath;
	}

	public void setAreaPath(String areaPath) {
		this.areaPath = areaPath;
	}

	public String getInternalPath() {
		return internalPath;
	}

	public void setInternalPath(String internalPath) {
		this.internalPath = internalPath;
	}

	public void setCdnDirectPath(String cdnDirectPath) {
		this.cdnDirectPath = cdnDirectPath;
	}

	public String getCdnDirectPath() {
		return cdnDirectPath;
	}

	public String getS3Path() {
		return s3Path;
	}

	public void setS3Path(String s3Path) {
		this.s3Path = s3Path;
	}

}
