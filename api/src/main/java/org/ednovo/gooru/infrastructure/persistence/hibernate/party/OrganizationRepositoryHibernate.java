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
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

//import com.google.gdata.client.Query;
import org.hibernate.Query;

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
	public List<Organization> listOrganization(Integer offset, Integer limit) {
		String hql = "FROM Organization";
		Query query = getSession().createQuery(hql);
		query.setFirstResult(offset);
        query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
        return (List) query.list();

	}
	
	@Override
	public Organization getOrganizationByIdpName(String idpDomainName) {
		String hql = "SELECT organizationDomainAssoc.organization FROM OrganizationDomainAssoc organizationDomainAssoc WHERE organizationDomainAssoc.domain.name = '" + idpDomainName + "'";
		return get(hql);
	}
	
	public Long getOrganizationCount() {
		String sql = "select  count(*) as count from organization";		
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
        return (Long) query.list().get(0);
	}


}
