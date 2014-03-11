/*******************************************************************************
 * DomainCio.java
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
package org.ednovo.gooru.core.cassandra.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.ednovo.gooru.core.constant.ColumnFamilyConstant;

@Entity(name = ColumnFamilyConstant.DOMAIN)
public class DomainCio implements IsEntityCassandraIndexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1948958471047901054L;

	private static final String ID = "id";

	@Id
	private String id;

	@Column
	private String boostLevel;

	@Override
	public String getIndexId() {
		return getId();
	}

	@Override
	public String getIndexType() {
		return ColumnFamilyConstant.DOMAIN;
	}

	@Override
	public Map<String, String> getRiFields() {
		Map<String, String> riFields = new HashMap<String, String>(1);
		riFields.put(ID, id);
		return riFields;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBoostLevel() {
		return boostLevel;
	}

	public void setBoostLevel(String boostLevel) {
		this.boostLevel = boostLevel;
	}
}
