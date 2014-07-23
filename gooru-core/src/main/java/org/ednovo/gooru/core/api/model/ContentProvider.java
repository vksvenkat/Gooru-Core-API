package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import org.ednovo.gooru.core.cassandra.model.IsCassandraIndexable;

public class ContentProvider  extends OrganizationModel implements Serializable,IsCassandraIndexable{

	
	private static final long serialVersionUID = 8760466116645806581L;
    
	private static final String INDEX_TYPE="content_provider";
	

	
	private String contentProviderUid;
	
	private String name;
	
	private CustomTableValue type;
	
	private boolean activeFlag;
	
	public String getContentProviderUid() {
		return contentProviderUid;
	}

	public void setContentProviderUid(String contentProviderUid) {
		this.contentProviderUid = contentProviderUid;
	}


	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	@Override
	public String getIndexType() {
		return INDEX_TYPE ;
	}

	@Override
	public String getIndexId() {
	return contentProviderUid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(CustomTableValue type) {
		this.type = type;
	}

	public CustomTableValue getType() {
		return type;
	}

}
