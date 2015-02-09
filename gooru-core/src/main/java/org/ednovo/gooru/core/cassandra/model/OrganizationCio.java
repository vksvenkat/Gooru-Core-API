package org.ednovo.gooru.core.cassandra.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.ednovo.gooru.core.constant.ColumnFamilyConstant;

@Entity(name = ColumnFamilyConstant.SCHOOL_DISTRICT)
public class OrganizationCio implements IsEntityCassandraIndexable {

	
	private static final long serialVersionUID = 4780363250879886267L;
	
	private static final String ID = "id";
	
	@Id
	private String organizationId;
	
	@Column
	private String organizationName;
	
    @Column
	private String organizationCode;
  
    @Column
    private String school;
       
    @Column
	private String parentId;
	
    @Column
	private String stateId;
      
    @Column
    private String stateName;
    
    @Column
    private String countryId;
    
    @Column
    private String countryName;
    
	
	public String getOrganizationCode() {
		return organizationCode;
	}

	public String getParentId() {
		return parentId;
	}

	
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	
	
	public String getOrganizationId() {
		return organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getSchool() {
		return school;
	}

	public String getStateId() {
		return stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getCountryId() {
		return countryId;
	}


	public String getCountryName() {
		return countryName;
	}

	
	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	@Override
	public String getIndexId() {
		return getOrganizationId();
	}

	
	@Override
	public String getIndexType() {
		return ColumnFamilyConstant.SCHOOL_DISTRICT;
	}

	@Override
	public Map<String, String> getRiFields() {
		Map<String, String> riFields = new HashMap<String, String>();
		riFields.put(ID,"organization.partyUid");
		return riFields;
	}


}
