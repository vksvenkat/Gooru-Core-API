/*******************************************************************************
 * RevisionHistory.java
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.ednovo.gooru.core.cassandra.model.IsEntityCassandraIndexable;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;

/**
 * @author Search Team
 * 
 */
@Entity(name = ColumnFamilyConstant.REVISION_HISTORY)
public class RevisionHistory implements IsEntityCassandraIndexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6712539094630655605L;


	@Id
	private String revisionHistoryUid;

	@Column
	private String entityName;

	@Column
	private String entityUid;

	@Column
	private String userUid;

	@Column
	private String onEvent;

	@Column
	private Date time;

	@Column
	private String data;

	public String getRevisionHistoryUid() {
		return revisionHistoryUid;
	}

	public void setRevisionHistoryUid(String revisionHistoryUid) {
		this.revisionHistoryUid = revisionHistoryUid;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityUid() {
		return entityUid;
	}

	public void setEntityUid(String entityUid) {
		this.entityUid = entityUid;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getOnEvent() {
		return onEvent;
	}

	public void setOnEvent(String onEvent) {
		this.onEvent = onEvent;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String getIndexId() {
		return getRevisionHistoryUid();
	}

	@Override
	public String getIndexType() {
		return ColumnFamilyConstant.REVISION_HISTORY;
	}

	@Override
	public Map<String, String> getRiFields() {
		Map<String, String> riFields = new HashMap<String, String>(1);
		riFields.put("key", revisionHistoryUid);
		return riFields;
	}

}
