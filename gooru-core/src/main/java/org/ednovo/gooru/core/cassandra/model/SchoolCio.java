package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.ednovo.gooru.core.constant.ColumnFamilyConstant;


@Entity(name = ColumnFamilyConstant.SCHOOL)
public class SchoolCio {
	
	@Column
	private String id;
	
	@Column
	private String name;
	
	@Column
	private String code;

	
	public String getId() {
		return id;
	}

	
	public String getName() {
		return name;
	}

	
	public String getCode() {
		return code;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}
	

}