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
