/////////////////////////////////////////////////////////////
// CustomTableRepositopryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.customTable;

import java.util.List;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomTableRepositopryHibernate extends BaseRepositoryHibernate implements CustomTableRepository {

	private final String RETIREVE_BY_NAME_VALUE = "From CustomTableValue ctv  where  ctv.value=:value  and  ctv.customTable.name=:name  and " + generateOrgAuthQuery("ctv.customTable.");
	private final String RETIREVE_BY_NAME = "From CustomTableValue ctv  where ctv.customTable.name=:name  and " + generateOrgAuthQuery("ctv.customTable.");
	private final String GET_FILTER_VALUE_FROM_CUSTOMTABLE = "From CustomTableValue ctv  where ctv.customTable.name=:name";
	private final String GET_VALUE_BY_DISPLAY_NAME = "From CustomTableValue ctv  where ctv.displayName =:displayName and ctv.customTable.name=:name";
	private final String GET_CUSTOM_TABLE_VALUES = "FROM  CustomTableValue ct where ct.customTable.name=:type";

	@SuppressWarnings("unchecked")
	@Override
	@Cacheable("gooruCache")
	public CustomTableValue getCustomTableValue(String name, String value) {
		Query query = getSessionReadOnly().createQuery(RETIREVE_BY_NAME_VALUE);
		query.setParameter("name", name);
		query.setParameter("value", value);
		addOrgAuthParameters(query);
		List<CustomTableValue> customValues = query.list();
		return (customValues.size() > 0) ? customValues.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Cacheable("gooruCache")
	public List<CustomTableValue> getCustomTableValues(String name) {
		Query query = getSessionReadOnly().createQuery(RETIREVE_BY_NAME);
		query.setParameter("name", name);
		addOrgAuthParameters(query);
		return query.list();

	}

	@SuppressWarnings("unchecked")
	@Override
	@Cacheable("gooruCache")
	public List<CustomTableValue> getFilterValueFromCustomTable(String name) {
		Query query = getSessionReadOnly().createQuery(GET_FILTER_VALUE_FROM_CUSTOMTABLE);
		query.setParameter("name", name);
		return query.list();
	}

	@Override
	@Cacheable("gooruCache")
	public CustomTableValue getValueByDisplayName(String displayName, String name) {
		Query query = getSessionReadOnly().createQuery(GET_VALUE_BY_DISPLAY_NAME);
		query.setParameter("name", name);
		query.setParameter("displayName", displayName);
		return (CustomTableValue) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	@Cacheable("gooruCache")
	public List<CustomTableValue> getCustomValues(String type) {
		Query query = getSessionReadOnly().createQuery(GET_CUSTOM_TABLE_VALUES);
		query.setParameter("type", type);
		return list(query);
	}

}
