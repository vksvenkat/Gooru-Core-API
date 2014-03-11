/*******************************************************************************
 * StorageArea.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
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

}
