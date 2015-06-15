package org.ednovo.gooru.domain.cassandra.service;


import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.cassandra.model.OrganizationCio;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchoolCassandraServiceImpl extends ApiCrudEntityCassandraServiceImpl<Organization,OrganizationCio>implements SchoolCassandraService {


	@Autowired
	private OrganizationRepository organizationRepository;
	
	
	@Override
	protected Organization fetchSource(String key) {
		Organization organization = getOrganizationRepository().getOrganizationByUid(key);
		if(organization.getType() != null){
		organization.setIndexType("schooldistrict");
		}
		return organization;
	}
     
	@Override
	String getDaoName() {
		return ColumnFamilyConstant.SCHOOL_DISTRICT;
	}


	public OrganizationRepository getOrganizationRepository() {
		return organizationRepository;
	}

	public void setOrganizationRepository(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}
	
}