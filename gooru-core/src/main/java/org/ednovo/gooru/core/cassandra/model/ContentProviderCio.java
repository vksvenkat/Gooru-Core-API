package org.ednovo.gooru.core.cassandra.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.ednovo.gooru.core.constant.ColumnFamilyConstant;

@Entity(name = ColumnFamilyConstant.CONTENT_PROVIDER)
public class ContentProviderCio implements IsEntityCassandraIndexable {

	
	private static final String ID = "id";
	
	@Id
	private String id;
  
	@Column
	private String name;
	
	@Column
	private String type;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getIndexId() {
		return getId();
	}

	@Override
	public String getIndexType() {
		return ColumnFamilyConstant.CONTENT_PROVIDER;
	}

	@Override
	public Map<String, String> getRiFields() {
		Map<String, String> riFields = new HashMap<String, String>();
		riFields.put(ID, id);
		return riFields;
	}

	

}
