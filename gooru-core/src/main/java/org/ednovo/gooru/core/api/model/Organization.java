/*******************************************************************************
 * Organization.java
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
