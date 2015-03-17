/////////////////////////////////////////////////////////////
// OrganizationRepositoryHibernate.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.infrastructure.persistence.hibernate.party;

import java.util.List;

import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationRepositoryHibernate extends BaseRepositoryHibernate implements OrganizationRepository,ParameterProperties, ConstantProperties {

	@Override
	public Organization getOrganizationByName(String partyName) {
		String hql = "FROM Organization organization WHERE organization.partyName = '" + partyName + "'";
		return get(hql);
	}

	@Override
	public Organization getOrganizationByCode(String organizationCode) {
		String hql = "FROM Organization organization WHERE organization.organizationCode = '" + organizationCode + "'";
		return get(hql);
	}

	@Override
	public Organization getOrganizationByUid(String organizationUid) {
		String hql = "FROM Organization organization WHERE organization.partyUid = '" + organizationUid + "'";
		return get(hql);
	}

	@Override
	public List<Organization> getOrganizations(String type, String parentOrganizationUid, String stateProvinceUid, Integer offset, Integer limit) {
		String hql = "SELECT o FROM Organization o  where 1 = 1";
		if (stateProvinceUid != null) {
			hql += " AND o.stateProvince.stateUid=:stateProvinceUid";
		}
		if (type != null) { 
			hql += " AND o.type.keyValue=:type";
		}
		if (parentOrganizationUid != null) { 
			hql += " AND o.parentOrganization.partyUid=:parentOrganizationUid";
		}
		Query query = getSession().createQuery(hql);
		
		if (stateProvinceUid != null) {
			query.setParameter("stateProvinceUid", stateProvinceUid);
		}
		if (type != null) {
			query.setParameter("type", type);
		}
		if (parentOrganizationUid != null) { 
			query.setParameter("parentOrganizationUid", parentOrganizationUid);
		}
		query.setFirstResult(offset);
        query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
        return (List) query.list();
	}
	
	@Override
	public Organization getOrganizationByIdpName(String idpDomainName) {
		String hql = "SELECT organizationDomainAssoc.organization FROM OrganizationDomainAssoc organizationDomainAssoc WHERE organizationDomainAssoc.domain.name = '" + idpDomainName + "'";
		return get(hql);
	}
	
	@Override
	public Long getOrganizationCount(String type, String parentOrganizationUid, String stateProvinceId) {
		String sql = " select count(1) as count from organization o left join state_province sp on o.state_province_uid = sp.state_province_uid  inner join custom_table_value ct on ct.custom_table_value_id = o.type_id where 1=1";		
		if (type != null) { 
			sql += " AND ct.key_value = '" + type + "'";
		}
		if (parentOrganizationUid != null) { 
			sql += " AND parent_organization_uid = '" + parentOrganizationUid + "'";
		}
		if (stateProvinceId != null){
			sql += " AND sp.state_province_uid = '" + stateProvinceId + "'";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
        return (Long) query.list().get(0);
	}

	@Override
	public List<Organization> getSchoolsByDistrictId(String type,String parentOrganizationUid, String stateProvinceUid) {
		String hql = "SELECT o FROM Organization o  where 1 = 1";
		if (stateProvinceUid != null) {
			hql += " AND o.stateProvince.stateUid=:stateProvinceUid";
		}
		if (type != null) { 
			hql += " AND o.type.value=:type";
		}
		if (parentOrganizationUid != null) { 
			hql += " AND o.parentOrganization.partyUid=:parentOrganizationUid";
		}
		Query query = getSession().createQuery(hql);
		
		if (stateProvinceUid != null) {
			query.setParameter("stateProvinceUid", stateProvinceUid);
		}
		if (type != null) {
			query.setParameter("type", type);
		}
		if (parentOrganizationUid != null) { 
			query.setParameter("parentOrganizationUid", parentOrganizationUid);
		}
	
		return (List) query.list();
	}
	


}
