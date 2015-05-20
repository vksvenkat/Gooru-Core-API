/////////////////////////////////////////////////////////////
// BaseRepositoryHibernate.java
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

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.SessionFactory;

/**
 * This class serves as the Base class for all other Daos - namely to hold
 * common methods that they might all use. Can be used for standard CRUD
 * operations.</p>
 */
public class BaseRepositoryHibernate extends AbstractRepositoryHibernate implements BaseRepository {

	@javax.annotation.Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;
	
	@javax.annotation.Resource(name = "sessionFactoryReadOnly")
	private SessionFactory sessionFactoryReadOnly;


	public SessionFactory getSessionFactoryReadOnly() {
		return sessionFactoryReadOnly;
	}
	
	@Override
	public void remove(Class<?> clazz, Serializable id) {
		delete(get(clazz, id));
	}

	@Override
	public void removeAll(Collection<?> entities) {
		deleteAll(entities);
	}

	public static String format(String inputString, Object... strings) {
		for (int i = 0; i < strings.length; i++) {
			
		}
		return String.format(inputString, strings);
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
}
