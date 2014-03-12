/////////////////////////////////////////////////////////////
// ApiCrudEntityCassandraServiceImpl.java
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
/**
 * 
 */
package org.ednovo.gooru.domain.cassandra.service;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.dao.EntityCassandraDao;
import org.ednovo.gooru.cassandra.core.service.CrudEntityCassandraServiceImpl;
import org.ednovo.gooru.core.cassandra.model.IsCassandraIndexable;
import org.ednovo.gooru.domain.cassandra.ApiCassandraFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author SearchTeam
 *
 */
public abstract class ApiCrudEntityCassandraServiceImpl<S extends IsCassandraIndexable, M extends Serializable> extends CrudEntityCassandraServiceImpl<S, M> {
	
	private EntityCassandraDao<M> entityCassandraDao;
	
	@Autowired
	private ApiCassandraFactory apiCassandraFactory;
	
	@PostConstruct
	protected final void init() {
		entityCassandraDao = (EntityCassandraDao<M>) apiCassandraFactory.get(getDaoName());
	}

	@Override
	protected final EntityCassandraDao<M> getCassandraDao() {
		return entityCassandraDao;
	}
	
	abstract String getDaoName();

}
