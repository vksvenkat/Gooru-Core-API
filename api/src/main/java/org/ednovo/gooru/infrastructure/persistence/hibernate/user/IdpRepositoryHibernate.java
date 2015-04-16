/////////////////////////////////////////////////////////////
// IdpRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.user;

import java.util.List;

import org.ednovo.gooru.core.api.model.Idp;
import org.ednovo.gooru.core.api.model.OrganizationDomainAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.IdpRepository;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.springframework.stereotype.Repository;
@Repository
public class IdpRepositoryHibernate extends BaseRepositoryHibernate implements IdpRepository {

	@Override
	public Idp findByName(String name) {
		 
		 List<Idp> idpList = getSession().createCriteria(Idp.class).add(Expression.eq("name", name)).list();		
		 return idpList.size()==0 ? null : idpList.get(0);
	}

	@Override
	public OrganizationDomainAssoc findByDomain(Idp domain) {
		String hql = " FROM OrganizationDomainAssoc domainOrgAssoc where domainOrgAssoc.domain.idpId ="+domain.getIdpId();
		Query query = getSession().createQuery(hql);
		List<OrganizationDomainAssoc> domainOrgAssocList = get(query);
		return domainOrgAssocList.size() == 0 ? null : domainOrgAssocList.get(0);
	}
	
}
