/////////////////////////////////////////////////////////////
// HibernateDaoSupport.java
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

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ednovo.gooru.core.api.model.Annotation;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.OrganizationWrapper;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserCredential;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class HibernateDaoSupport extends UserGroupSupport {

	public void saveOrUpdate(Object model) {
		if (model instanceof Annotation) {
			if (((Annotation) model).getResource() != null) {
				((Content) model).setOrganization(((Annotation) model).getResource().getOrganization());
				((Content) model).setVersion(((Content) model).getVersion() == null ? 1 : ((Content) model).getVersion() + 1);
			}

		} else if (model instanceof OrganizationWrapper && ((OrganizationWrapper) model).getOrganization() == null) {
			((OrganizationWrapper) model).setOrganization(getCurrentUserPrimaryOrganization());
		}

		if (model instanceof Content) {
			((Content) model).setLastModified(new Date(System.currentTimeMillis()));
			((Content) model).setVersion(((Content) model).getVersion() == null ? 1 : ((Content) model).getVersion() + 1);
		}

		if (model instanceof CollectionItem) {
			((CollectionItem) model).getCollection().setLastModified(new Date(System.currentTimeMillis()));
			((CollectionItem) model).getCollection().setVersion(((CollectionItem) model).getCollection().getVersion() == null ? 1 : ((CollectionItem) model).getCollection().getVersion() + 1);

		}

		if (model instanceof User) {
			((User) model).setLastModifiedOn(new Date(System.currentTimeMillis()));
		}

		getSession().saveOrUpdate(model);

	}

	public void delete(Object object) {
		getSession().delete(object);
	}

	public void deleteAll(Collection<?> entities) {
		Iterator<?> iterator = entities.iterator();
		while (iterator.hasNext()) {
			getSession().delete(iterator.next());
		}
	}

	public void saveOrUpdateAll(Collection<?> entities) {
		Iterator<?> iterator = entities.iterator();
		while (iterator.hasNext()) {
			saveOrUpdate(iterator.next());
		}
	}

	public List<?> find(String query, Integer param1) {
		return getSession().createQuery(query).setParameter(0, param1).list();
	}

	public List<?> find(String query, String param1) {
		return getSession().createQuery(query).setParameter(0, param1).list();
	}

	public List<?> find(String query, Long param1) {
		return getSession().createQuery(query).setParameter(0, param1).list();
	}

	@SuppressWarnings("rawtypes")
	public List find(String hql) {
		return getSession().createQuery(hql).list();
	}

	public List<?> findFirstNRows(String hql, int rows) {
		return getSession().createQuery(hql).setFetchSize(rows).list();
	}

	public <T> T get(String hql) {
		Query query = getSession().createQuery(hql);
		List<T> datas = list(query);
		return datas.size() > 0 ? datas.get(0) : null;
	}

	public Session getSession() {

		Session currentSession = null;
		try {
			currentSession = getSessionFactory().getCurrentSession();
		} catch (Exception e) {
			currentSession = getSessionFactory().openSession();
		}
		currentSession.enableFilter("customFieldFilter").setParameterList("customFieldFilterParam", Constants.customFieldsKey);
		return currentSession;
	}

	public Session getSessionReadOnly() {

		Session currentSession = null;
		try {
			currentSession = getSessionFactoryReadOnly().getCurrentSession();
		} catch (Exception e) {
			currentSession = getSessionFactoryReadOnly().openSession();
		}
		currentSession.enableFilter("customFieldFilter").setParameterList("customFieldFilterParam", Constants.customFieldsKey);
		return currentSession;
	}

	protected Organization getCurrentUserOrganization() {

		UserCredential credential = getUserCredential();

		if (credential != null && credential.getOrganizationUid() != null) {
			return (Organization) getSession().get(Organization.class, credential.getOrganizationUid());
		}

		return null;
	}

	protected Organization getCurrentUserPrimaryOrganization() {

		UserCredential credential = getUserCredential();

		if (credential != null && credential.getOrganizationUid() != null) {
			return (Organization) getSession().get(Organization.class, credential.getPrimaryOrganizatoinUid());
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> list(Query query) { 
	    return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T[]> arrayList(Query query) { 
	    return query.list();
	}

	public abstract SessionFactory getSessionFactory();

	public abstract SessionFactory getSessionFactoryReadOnly();

}
