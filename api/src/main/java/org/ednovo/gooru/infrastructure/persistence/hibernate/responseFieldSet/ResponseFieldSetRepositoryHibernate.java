/////////////////////////////////////////////////////////////
// ResponseFieldSetRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.responseFieldSet;
import java.util.List;

import org.ednovo.gooru.core.api.model.ResponseFieldSet;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class ResponseFieldSetRepositoryHibernate extends BaseRepositoryHibernate implements ResponseFieldSetRepository {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public ResponseFieldSetRepositoryHibernate(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate) {
		super();
		setSessionFactory(sessionFactory);
		setJdbcTemplate(jdbcTemplate);
	}
	
	@Override
	public ResponseFieldSet getResponseFieldSet(String fieldSetId, String gooruUId) {
		
		String hql = "FROM ResponseFieldSet responseFieldSet WHERE responseFieldSet.fieldSetId = :fieldSetId AND responseFieldSet.gooruUId = :gooruUId AND  "+ generateOrgAuthQuery("responseFieldSet.");
		Query query = getSession().createQuery(hql);
		query.setParameter("fieldSetId", fieldSetId);
		query.setParameter("gooruUId", gooruUId);
		addOrgAuthParameters(query);
		List<ResponseFieldSet> responseFields = (List<ResponseFieldSet>)query.list();
		return responseFields.size() > 0 ? responseFields.get(0) : null;
		
	}

	@Override
	public ResponseFieldSet deleteResponseFieldSet(String fieldSetId) {
		String sql = "DELETE FROM response_fields WHERE field_id ='"+fieldSetId+"' AND organization IN ("+getUserOrganizationUidsAsString()+")";
		jdbcTemplate.execute(sql);
		return null;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<ResponseFieldSet> getResponseFieldSet() {
		
		String hql = "FROM ResponseFieldSet responseFieldSet WHERE  " + generateOrgAuthQueryWithData("responseFieldSet.");
		Query query = getSession().createQuery(hql);
		List<ResponseFieldSet> responseFields = (List<ResponseFieldSet>)query.list();
		//return (List<ResponseField>) (responseFields.size() > 0 ? responseFields.get(0) : null);
		return responseFields.size() == 0 ? null : responseFields;
	}

	
}
