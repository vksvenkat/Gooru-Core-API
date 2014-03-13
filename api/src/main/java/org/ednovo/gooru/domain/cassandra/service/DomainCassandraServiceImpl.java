/////////////////////////////////////////////////////////////
// DomainCassandraServiceImpl.java
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
package org.ednovo.gooru.domain.cassandra.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ednovo.gooru.core.cassandra.model.DomainCio;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.springframework.stereotype.Service;

@Service
public class DomainCassandraServiceImpl extends ApiEntityCassandraServiceImpl<DomainCio> implements DomainCassandraService{

	@Override
	public DomainCio saveObject(DomainCio domainCio) {
		if(domainCio!= null){
			getCassandraDao().save(domainCio);
		}
		return domainCio;
	}

	@Override
	public List<DomainCio> save(String... names) {
		if (names != null) {
			List<DomainCio> resourceSourceCios = new ArrayList<DomainCio>();
			Collection<String> modelKeys = new ArrayList<String>();
			for (String name : names) {
				DomainCio resourceSourceCio = new DomainCio();
				resourceSourceCio.setId(name);
				resourceSourceCio.setBoostLevel("1.0");
				resourceSourceCios.add(resourceSourceCio);
				modelKeys.add(name);
			}
			save(resourceSourceCios, modelKeys);
			return resourceSourceCios;
		}
		return null;
	}

	@Override
	public DomainCio save(String id) {
		return null;
	}

	@Override
	String getDaoName() {
		return ColumnFamilyConstant.DOMAIN;
	}

}
