/*******************************************************************************
 * ShelfItem.java
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

import org.ednovo.gooru.core.api.model.OrganizationModel;
import org.ednovo.gooru.core.api.model.Resource;


public class ShelfItem extends OrganizationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1117655899231660702L;


	private String shelfItemId;
	
	private Shelf shelf;
	
	private Resource resource;
	
	private String addedType;
	
	private Date createdOn;
	
	private Date lastActivityOn;
	
	public String getShelfItemId() {
		return shelfItemId;
	}

	public void setShelfItemId(String shelfItemId) {
		this.shelfItemId = shelfItemId;
	}
	
	public String getAddedType() {
		return addedType;
	}

	public void setAddedType(String addedType) {
		this.addedType = addedType;
	}

	public Shelf getShelf() {
		return shelf;
	}

	public void setShelf(Shelf shelf) {
		this.shelf = shelf;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setLastActivityOn(Date lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}

	public Date getLastActivityOn() {
		return lastActivityOn;
	}
}
