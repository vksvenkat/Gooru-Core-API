/////////////////////////////////////////////////////////////
// AbstractRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.Collection;
import java.util.List;

import org.ednovo.gooru.core.api.model.OrganizationModel;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class serves as the Base class for all other Daos - namely to hold
 * common methods that they might all use. Can be used for standard CRUD
 * operations.</p>
 */
public abstract class AbstractRepositoryHibernate extends AuthorizationDaoSupport implements BaseRepository {

	private final Logger logger = LoggerFactory.getLogger(AbstractRepositoryHibernate.class);

	@Override
	public void save(Object o) {
		saveOrUpdate(o);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getPagedResults(Class<?> clazz, int page, int recordPerPage) {
		
		Criteria criteria = getSession().createCriteria(clazz);
		if (clazz.isAssignableFrom(OrganizationModel.class)) {
			criteria.add(Restrictions.in("organization.partyUid", getPartyPermits()));
		}
		criteria.setFirstResult(recordPerPage * (page - 1));
		criteria.setMaxResults(recordPerPage);
		return criteria.list();
	}

	public void remove(Object o) {
		delete(o);
	}

	public void removeAll(Collection<?> entities) {
		deleteAll(entities);
	}

	public void saveAll(Collection<?> entities) {
		saveOrUpdateAll(entities);
	}

	public void flush() {
		getSession().flush();
	}

	public void clear() {
		getSession().clear();
	}

	public Logger getLogger() {
		return logger;
	}

	

}
